/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.config.ApplicationConfig;
import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.converters.ModelConverter;
import eca.converters.model.ClassificationModel;
import eca.core.evaluation.Evaluation;
import eca.data.FileUtils;
import eca.ensemble.EnsembleClassifier;
import eca.gui.PanelBorderUtils;
import eca.gui.choosers.SaveModelChooser;
import eca.gui.choosers.SaveResultsChooser;
import eca.gui.dictionary.ClassificationModelDictionary;
import eca.gui.listeners.ReferenceListener;
import eca.gui.logging.LoggerUtils;
import eca.gui.panels.ClassifyInstancePanel;
import eca.gui.panels.ROCCurvePanel;
import eca.gui.service.ClassifierIndexerService;
import eca.gui.tables.ClassificationCostsMatrix;
import eca.gui.tables.ClassifyInstanceTable;
import eca.gui.tables.EnsembleTable;
import eca.gui.tables.EvaluationStatisticsTableFactory;
import eca.gui.tables.JDataTableBase;
import eca.gui.tables.LogisticCoefficientsTable;
import eca.gui.tables.MisClassificationMatrix;
import eca.gui.tables.SignificantAttributesTable;
import eca.gui.tables.models.EvaluationStatisticsModel;
import eca.neural.NetworkVisualizer;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.report.ReportGenerator;
import eca.report.evaluation.AbstractEvaluationReportService;
import eca.report.evaluation.AttachmentImage;
import eca.report.evaluation.EvaluationReport;
import eca.report.evaluation.html.EvaluationHtmlReportService;
import eca.report.evaluation.xls.EvaluationXlsReportService;
import eca.roc.AttributesSelection;
import eca.roc.RocCurve;
import eca.trees.DecisionTreeClassifier;
import eca.trees.J48;
import eca.trees.TreeVisualizer;
import eca.util.Entry;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classification results frame.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ClassificationResultsFrameBase extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();
    private static final ApplicationConfig APPLICATION_CONFIG = CONFIG_SERVICE.getApplicationConfig();

    private static final String RESULTS_TEXT = "Результаты классификации";
    private static final String STATISTICS_TEXT = "Статистика";
    private static final String MATRIX_TEXT = "Матрица классификации";
    private static final String ROC_CURVES_TEXT = "ROC кривые";
    private static final String CLASSIFY_TAB_TITLE = "Классификация";
    private static final String TREE_STRUCTURE_TAB_TITLE = "Структура дерева";
    private static final String NETWORK_STRUCTURE_TAB_TITLE = "Структура нейронной сети";
    private static final String LOGISTIC_COEFFICIENTS_TAB_TITLE = "Оценки коэффициентов";
    private static final String SIGNIFICANT_ATTRIBUTES_TAB_TITLE = "Значимые атрибуты";
    private static final String ENSEMBLE_STRUCTURE_TAB_TITLE = "Структура ансамбля";
    private static final String SAVE_RESULTS_BUTTON_TEXT = "Сохранить";
    private static final int DEFAULT_WIDTH = 875;
    private static final int DEFAULT_HEIGHT = 650;
    private static final String FILE_MENU_TEXT = "Файл";
    private static final String SERVICE_MENU_TEXT = "Сервис";
    private static final String REFERENCE_MENU_TEXT = "Справка";
    private static final String SAVE_MODEL_MENU_TEXT = "Сохранить модель";
    private static final String INPUT_OPTIONS_MENU_TEXT = "Входные параметры модели";
    private static final String SHOW_REFERENCE_MENU_TEXT = "Показать справку";
    private static final String INITIAL_DATA_MENU_TEXT = "Исходные данные";
    private static final String ATTR_STATISTICS_MENU_TEXT = "Статистика по атрибутам";
    private static final int ATTACHMENT_TAB_INDEX = 3;
    private static final String HTML_EXTENSION = ".html";
    private static final String INVALID_REPORT_EXTENSION = "Система не поддерживает отчеты с расширением %s";

    private static final String REFERENCE_KEY_STROKE = "F1";
    private static final String SAVE_MODEL_KEY_STROKE = "ctrl S";

    private final Date creationDate = new Date();
    private final Classifier classifier;
    private final Instances data;
    private final Evaluation evaluation;
    private final int digits;
    private JTabbedPane pane;
    private JScrollPane resultPane;

    private EvaluationStatisticsModel evaluationStatisticsModel;

    private ClassificationCostsMatrix costMatrix;
    private JFrame parentFrame;

    private ROCCurvePanel rocCurvePanel;

    public ClassificationResultsFrameBase(JFrame parent, String title, Classifier classifier, Instances data,
                                          Evaluation evaluation, int digits) {
        this.classifier = classifier;
        this.data = data;
        this.setTitle(title);
        this.parentFrame = parent;
        this.setIconImage(parent.getIconImage());
        this.evaluation = evaluation;
        this.digits = digits;
        this.createGUI();
        this.createMenuBar();
        this.setLocationRelativeTo(parent);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Classifier classifier() {
        return classifier;
    }

    public Instances data() {
        return data;
    }

    public Evaluation evaluation() {
        return evaluation;
    }

    public void addPanel(String title, Component panel) {
        pane.add(title, panel);
    }

    public void setEvaluationStatisticsModel(EvaluationStatisticsModel evaluationStatisticsModel) {
        this.evaluationStatisticsModel = evaluationStatisticsModel;
        JTable evaluationStatisticsTable = createEvaluationStatisticsTable();
        resultPane.setViewportView(evaluationStatisticsTable);
    }

    private JTable createEvaluationStatisticsTable() {
        JDataTableBase dataTableBase = new JDataTableBase(evaluationStatisticsModel);
        dataTableBase.setAutoResizeOff(false);
        dataTableBase.setRowSelectionAllowed(false);
        dataTableBase.setToolTipText(ReportGenerator.getClassifierInputOptionsAsHtml(classifier, false));
        return dataTableBase;
    }

    private void createMenuBar() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        JMenu serviceMenu = new JMenu(SERVICE_MENU_TEXT);
        JMenu helpMenu = new JMenu(REFERENCE_MENU_TEXT);
        JMenuItem saveModelMenu = new JMenuItem(SAVE_MODEL_MENU_TEXT);
        saveModelMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SAVE_ICON)));
        JMenuItem inputMenu = new JMenuItem(INPUT_OPTIONS_MENU_TEXT);
        JMenuItem refMenu = new JMenuItem(SHOW_REFERENCE_MENU_TEXT);
        refMenu.setAccelerator(KeyStroke.getKeyStroke(REFERENCE_KEY_STROKE));
        JMenuItem dataMenu = new JMenuItem(INITIAL_DATA_MENU_TEXT);
        dataMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DATA_ICON)));
        JMenuItem statMenu = new JMenuItem(ATTR_STATISTICS_MENU_TEXT);
        statMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.STATISTICS_ICON)));

        saveModelMenu.setAccelerator(KeyStroke.getKeyStroke(SAVE_MODEL_KEY_STROKE));

        saveModelMenu.addActionListener(new SaveModelListener());
        inputMenu.addActionListener(new ActionListener() {

            HtmlFrame inputParamInfo;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (inputParamInfo == null) {
                    inputParamInfo = new HtmlFrame(inputMenu.getText(),
                            ReportGenerator.getClassifierInputOptionsAsHtml(classifier, true),
                            ClassificationResultsFrameBase.this);
                    ClassificationResultsFrameBase.this.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent evt) {
                            inputParamInfo.dispose();
                        }
                    });
                }
                inputParamInfo.setVisible(true);
            }
        });
        refMenu.addActionListener(new ReferenceListener(ClassificationResultsFrameBase.this));
        dataMenu.addActionListener(new ActionListener() {

            InstancesFrame dataFrame;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataFrame == null) {
                    dataFrame = new InstancesFrame(data, ClassificationResultsFrameBase.this);
                    ClassificationResultsFrameBase.this.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent evt) {
                            dataFrame.dispose();
                        }
                    });
                }
                dataFrame.setVisible(true);
            }
        });

        statMenu.addActionListener(new ActionListener() {

            AttributesStatisticsFrame frame;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (frame == null) {
                    frame = new AttributesStatisticsFrame(data, ClassificationResultsFrameBase.this, digits);
                }
                frame.setVisible(true);
            }
        });

        fileMenu.add(saveModelMenu);
        serviceMenu.add(dataMenu);
        serviceMenu.add(inputMenu);
        serviceMenu.add(statMenu);
        helpMenu.add(refMenu);
        menu.add(fileMenu);
        menu.add(serviceMenu);
        menu.add(helpMenu);
        this.setJMenuBar(menu);
    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public void populateAdditionalResults() throws Exception {
        if (classifier instanceof DecisionTreeClassifier) {
            JScrollPane pane
                    = new JScrollPane(
                    new TreeVisualizer((DecisionTreeClassifier) classifier, digits));
            addPanel(TREE_STRUCTURE_TAB_TITLE, pane);
            JScrollBar bar = pane.getHorizontalScrollBar();
            bar.setValue(bar.getMaximum());
        } else if (classifier instanceof NeuralNetwork) {
            NeuralNetwork net = (NeuralNetwork) classifier;
            JScrollPane pane = new JScrollPane(new NetworkVisualizer(net, this, digits));
            addPanel(NETWORK_STRUCTURE_TAB_TITLE, pane);
        } else if (classifier instanceof Logistic) {
            LogisticCoefficientsTable logisticCoefficientsTable =
                    new LogisticCoefficientsTable((Logistic) classifier, data, digits);
            JScrollPane pane = new JScrollPane(logisticCoefficientsTable);
            addPanel(LOGISTIC_COEFFICIENTS_TAB_TITLE, pane);
            AttributesSelection attributesSelection = new AttributesSelection(data);
            attributesSelection.setAucThresholdValue(APPLICATION_CONFIG.getAucThresholdValue());
            attributesSelection.calculate();
            SignificantAttributesTable signTable
                    = new SignificantAttributesTable(attributesSelection, digits);
            JScrollPane signPane = new JScrollPane(signTable);
            addPanel(SIGNIFICANT_ATTRIBUTES_TAB_TITLE, signPane);
        } else if (classifier instanceof EnsembleClassifier) {
            EnsembleClassifier ensembleClassifier = (EnsembleClassifier) classifier;
            EnsembleTable ensembleTable =
                    new EnsembleTable(ensembleClassifier.getStructure(), getParentFrame(), digits);
            JScrollPane pane = new JScrollPane(ensembleTable);
            addPanel(ENSEMBLE_STRUCTURE_TAB_TITLE, pane);
        } else if (classifier instanceof J48) {
            J48 j48 = (J48) classifier;
            addPanel(TREE_STRUCTURE_TAB_TITLE,
                    new weka.gui.treevisualizer.TreeVisualizer(null, j48.graph(), new PlaceNode2()));
        }
    }

    public static ClassificationResultsFrameBase buildClassificationResultsFrameBase(JFrame parentFrame, String title,
                                                                                     Classifier classifier,
                                                                                     Instances data,
                                                                                     Evaluation evaluation, int digits)
            throws Exception {
        ClassificationResultsFrameBase classificationResultsFrameBase =
                new ClassificationResultsFrameBase(parentFrame, title, classifier, data, evaluation, digits);
        EvaluationStatisticsModel evaluationStatisticsTable =
                EvaluationStatisticsTableFactory.buildEvaluationStatisticsTable(classifier, evaluation, digits);
        classificationResultsFrameBase.setEvaluationStatisticsModel(evaluationStatisticsTable);
        classificationResultsFrameBase.populateAdditionalResults();
        return classificationResultsFrameBase;
    }

    private void createGUI() {
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        pane = new JTabbedPane();
        JPanel resultPanel = new JPanel(new GridBagLayout());
        resultPane = new JScrollPane();
        resultPane.setBorder(PanelBorderUtils.createTitledBorder(STATISTICS_TEXT));
        MisClassificationMatrix misClassificationMatrix = new MisClassificationMatrix(evaluation);
        JScrollPane misClassPane = new JScrollPane(misClassificationMatrix);
        misClassPane.setBorder(PanelBorderUtils.createTitledBorder(MATRIX_TEXT));

        costMatrix = new ClassificationCostsMatrix(evaluation, digits);
        JScrollPane costsPane = new JScrollPane(costMatrix);
        costsPane.setBorder(PanelBorderUtils.
                createTitledBorder(RESULTS_TEXT));

        resultPanel.add(resultPane, new GridBagConstraints(0, 0, 1, 1, 1, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        resultPanel.add(costsPane, new GridBagConstraints(0, 1, 1, 1, 1, 0.25,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        resultPanel.add(misClassPane, new GridBagConstraints(0, 2, 1, 1, 1, 0.25,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));

        JButton saveButton = new JButton(SAVE_RESULTS_BUTTON_TEXT);
        saveButton.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SAVE_ICON)));
        Dimension dim = new Dimension(150, 25);
        saveButton.setPreferredSize(dim);
        saveButton.setMinimumSize(dim);
        resultPanel.add(saveButton, new GridBagConstraints(0, 3, 1, 1, 1, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));

        saveButton.addActionListener(new SaveReportListener());

        rocCurvePanel = new ROCCurvePanel(new RocCurve(evaluation), this, digits);

        pane.add(RESULTS_TEXT, resultPanel);
        pane.add(CLASSIFY_TAB_TITLE, new ClassifyInstancePanel(
                new ClassifyInstanceTable(data, digits), classifier));
        pane.add(ROC_CURVES_TEXT, rocCurvePanel);
        this.add(pane);
    }

    private Map<String, String> createStatisticsMap() {
        return evaluationStatisticsModel.getResults().stream().collect(
                Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private List<AttachmentImage> createAttachmentImagesList() {
        List<AttachmentImage> attachmentImages = new ArrayList<>();
        try {
            attachmentImages.add(new AttachmentImage(ROC_CURVES_TEXT, rocCurvePanel.createImage()));
            if (classifier instanceof DecisionTreeClassifier) {
                JScrollPane scrollPane = (JScrollPane) pane.getComponent(ATTACHMENT_TAB_INDEX);
                TreeVisualizer treeVisualizer = (TreeVisualizer) scrollPane.getViewport().getView();
                attachmentImages.add(new AttachmentImage(TREE_STRUCTURE_TAB_TITLE, treeVisualizer.getImage()));
            } else if (classifier instanceof NeuralNetwork) {
                JScrollPane scrollPane = (JScrollPane) pane.getComponent(ATTACHMENT_TAB_INDEX);
                NetworkVisualizer networkVisualizer = (NetworkVisualizer) scrollPane.getViewport().getView();
                attachmentImages.add(new AttachmentImage(NETWORK_STRUCTURE_TAB_TITLE, networkVisualizer.getImage()));
            }
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
            JOptionPane.showMessageDialog(ClassificationResultsFrameBase.this, ex.getMessage(),
                    null, JOptionPane.WARNING_MESSAGE);
        }
        return attachmentImages;
    }

    /**
     * Save classifier model action listener
     */
    private class SaveModelListener implements ActionListener {

        SaveModelChooser fileChooser;

        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                if (fileChooser == null) {
                    fileChooser = new SaveModelChooser();
                }
                fileChooser.setSelectedFile(new File(ClassifierIndexerService.getIndex(classifier())));
                File file = fileChooser.getSelectedFile(ClassificationResultsFrameBase.this);
                if (file != null) {
                    Map<String, String> props = buildPropertiesMap();
                    ModelConverter.saveModel(file,
                            new ClassificationModel((AbstractClassifier) classifier, data, evaluation, props));
                }
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(ClassificationResultsFrameBase.this, e.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }

        Map<String, String> buildPropertiesMap() {
            Map<String, String> properties = new HashMap<>();
            properties.put(ClassificationModelDictionary.DESCRIPTION_KEY, getTitle());
            properties.put(ClassificationModelDictionary.DIGITS_KEY, String.valueOf(digits));
            return properties;
        }
    }

    /**
     * Action listener for saving evaluation results report.
     */
    private class SaveReportListener implements ActionListener {

        SaveResultsChooser chooser;

        @Override
        public void actionPerformed(ActionEvent event) {
            File file;
            try {
                if (chooser == null) {
                    chooser = new SaveResultsChooser();
                }
                chooser.setSelectedFile(new File(ClassifierIndexerService.getResultsIndex(classifier())));
                file = chooser.getSelectedFile(ClassificationResultsFrameBase.this);
                if (file != null) {
                    saveReportToFile(file);
                }
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
                JOptionPane.showMessageDialog(ClassificationResultsFrameBase.this, ex.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }

        void saveReportToFile(File file) throws Exception {
            AbstractEvaluationReportService reportService;
            if (FileUtils.isXlsExtension(file.getName())) {
                reportService = new EvaluationXlsReportService();
            } else if (file.getName().endsWith(HTML_EXTENSION)) {
                reportService = new EvaluationHtmlReportService();
            } else {
                throw new IllegalArgumentException(String.format(INVALID_REPORT_EXTENSION, file.getName()));
            }
            reportService.setFile(file);
            reportService.setDecimalFormat(costMatrix.getFormat());
            EvaluationReport evaluationReport =
                    new EvaluationReport(createStatisticsMap(), data, evaluation, classifier,
                            createAttachmentImagesList());
            reportService.setEvaluationReport(evaluationReport);
            reportService.saveReport();
        }
    }
}
