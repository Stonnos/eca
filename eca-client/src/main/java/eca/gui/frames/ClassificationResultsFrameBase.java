/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.Reference;
import eca.converters.DataFileExtension;
import eca.converters.ModelConverter;
import eca.core.evaluation.Evaluation;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.EnsembleClassifier;
import eca.ensemble.StackingClassifier;
import eca.gui.PanelBorderUtils;
import eca.gui.choosers.SaveModelChooser;
import eca.gui.choosers.SaveResultsChooser;
import eca.gui.logging.LoggerUtils;
import eca.gui.panels.ClassifyInstancePanel;
import eca.gui.panels.ROCCurvePanel;
import eca.gui.service.ClassifierIndexerService;
import eca.gui.service.ClassifierInputOptionsService;
import eca.gui.tables.*;
import eca.model.InputData;
import eca.model.ModelDescriptor;
import eca.neural.NetworkVisualizer;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.roc.AttributesSelection;
import eca.roc.RocCurve;
import eca.trees.DecisionTreeClassifier;
import eca.trees.TreeVisualizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Classification results frame.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ClassificationResultsFrameBase extends JFrame {

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
    private static final String ATTR_INFO_MENU_TEXT = "Информация об атрибутах";
    private static final String INITIAL_DATA_MENU_TEXT = "Исходные данные";
    private static final String ATTR_STATISTICS_MENU_TEXT = "Статистика по атрибутам";

    private final Classifier classifier;
    private final Instances data;
    private final Evaluation evaluation;
    private final ClassifierIndexerService indexer = new ClassifierIndexerService();
    private JTabbedPane pane;
    private JScrollPane resultPane;

    private JTable statTable;
    private JTable misMatrix;
    private ClassificationCostsMatrix costMatrix;
    private JFrame parent;

    private ROCCurvePanel rocCurvePanel;

    public ClassificationResultsFrameBase(JFrame parent, String title, Classifier classifier, Instances data,
                                          Evaluation evaluation, final int digits)
            throws Exception {
        this.classifier = classifier;
        this.data = data;
        this.setTitle(title);
        this.parent = parent;
        this.setIconImage(parent.getIconImage());
        this.evaluation = evaluation;
        this.makeGUI(digits);
        this.makeMenu(digits);
        this.setLocationRelativeTo(parent);
    }

    public ClassifierIndexerService getIndexer() {
        return indexer;
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

    public final void addPanel(String title, Component panel) {
        pane.add(title, panel);
    }

    public final void setStatisticsTable(JTable table) {
        this.statTable = table;
        this.statTable.setRowSelectionAllowed(false);
        this.statTable.setToolTipText(ClassifierInputOptionsService.getInputOptionsInfoAsHtml(classifier));
        resultPane.setViewportView(table);
    }

    private void makeMenu(final int digits) {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        JMenu serviceMenu = new JMenu(SERVICE_MENU_TEXT);
        JMenu helpMenu = new JMenu(REFERENCE_MENU_TEXT);
        JMenuItem saveModelMenu = new JMenuItem(SAVE_MODEL_MENU_TEXT);
        JMenuItem inputMenu = new JMenuItem(INPUT_OPTIONS_MENU_TEXT);
        JMenuItem refMenu = new JMenuItem(SHOW_REFERENCE_MENU_TEXT);
        refMenu.setAccelerator(KeyStroke.getKeyStroke("F1"));
        JMenuItem attrMenu = new JMenuItem(ATTR_INFO_MENU_TEXT);
        JMenuItem dataMenu = new JMenuItem(INITIAL_DATA_MENU_TEXT);
        JMenuItem statMenu = new JMenuItem(ATTR_STATISTICS_MENU_TEXT);
        //--------------------------------------------
        saveModelMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        //--------------------------------------------
        saveModelMenu.addActionListener(new ActionListener() {

            SaveModelChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new SaveModelChooser();
                    }
                    fileChooser.setSelectedFile(new File(indexer.getIndex(classifier())));
                    File file = fileChooser.getSelectedFile(ClassificationResultsFrameBase.this);
                    if (file != null) {
                        InputData inputData = new InputData((AbstractClassifier) classifier, data);
                        ModelConverter.saveModel(file,
                                new ModelDescriptor(inputData, evaluation, getTitle(), digits));
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(ClassificationResultsFrameBase.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //--------------------------------------------
        inputMenu.addActionListener(new ActionListener() {

            TextInfoFrame inputParamInfo;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (inputParamInfo == null) {
                    inputParamInfo = new TextInfoFrame(inputMenu.getText(),
                            ClassifierInputOptionsService.getInputOptionsInfo(classifier), ClassificationResultsFrameBase.this);
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
        //--------------------------------------------
        attrMenu.addActionListener(new ActionListener() {

            TextInfoFrame attributesInfo;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (attributesInfo == null) {
                    attributesInfo = new TextInfoFrame(attrMenu.getText(),
                            getAttributesInfo(), ClassificationResultsFrameBase.this);
                    ClassificationResultsFrameBase.this.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent evt) {
                            attributesInfo.dispose();
                        }
                    });
                }
                attributesInfo.setVisible(true);
            }
        });
        //--------------------------------------------
        refMenu.addActionListener(new ActionListener() {

            Reference ref;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (ref == null) {
                        ref = new Reference();
                    }
                    ref.openReference();
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(ClassificationResultsFrameBase.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        //-------------------------------------------------
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
        //-------------------------------------------------
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
        //--------------------------------------------
        fileMenu.add(saveModelMenu);
        serviceMenu.add(dataMenu);
        serviceMenu.add(inputMenu);
        serviceMenu.add(attrMenu);
        serviceMenu.add(statMenu);
        helpMenu.add(refMenu);
        menu.add(fileMenu);
        menu.add(serviceMenu);
        menu.add(helpMenu);
        this.setJMenuBar(menu);
    }

    private void makeGUI(final int digits) throws Exception {
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        pane = new JTabbedPane();
        JPanel resultPanel = new JPanel(new GridBagLayout());
        //------------------------------------------------------
        resultPane = new JScrollPane();
        resultPane.setBorder(PanelBorderUtils.createTitledBorder(STATISTICS_TEXT));
        //------------------------------------------------------
        misMatrix = new MisClassificationMatrix(evaluation);
        JScrollPane misClassPane = new JScrollPane(misMatrix);
        misClassPane.setBorder(PanelBorderUtils.createTitledBorder(MATRIX_TEXT));
        //----------------------------------------
        costMatrix = new ClassificationCostsMatrix(evaluation, digits);
        JScrollPane costsPane = new JScrollPane(costMatrix);
        costsPane.setBorder(PanelBorderUtils.
                createTitledBorder(RESULTS_TEXT));
        //---------------------------------
        resultPanel.add(resultPane, new GridBagConstraints(0, 0, 1, 1, 1, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        resultPanel.add(costsPane, new GridBagConstraints(0, 1, 1, 1, 1, 0.25,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        resultPanel.add(misClassPane, new GridBagConstraints(0, 2, 1, 1, 1, 0.25,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        //-----------------------------------
        JButton saveButton = new JButton(SAVE_RESULTS_BUTTON_TEXT);
        Dimension dim = new Dimension(150, 25);
        saveButton.setPreferredSize(dim);
        saveButton.setMinimumSize(dim);
        resultPanel.add(saveButton, new GridBagConstraints(0, 3, 1, 1, 1, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
        //----------------------------------
        saveButton.addActionListener(new ActionListener() {

            SaveResultsChooser chooser;
            XlsResultsSaver xlsResultsSaver;

            @Override
            public void actionPerformed(ActionEvent evt) {
                File file;
                try {
                    if (chooser == null) {
                        chooser = new SaveResultsChooser();
                    }
                    chooser.setSelectedFile(new File(indexer.getResultsIndex(classifier())));
                    file = chooser.getSelectedFile(ClassificationResultsFrameBase.this);
                    if (file != null) {
                        if (xlsResultsSaver == null) {
                            xlsResultsSaver = new XlsResultsSaver();
                        }
                        xlsResultsSaver.save(file);
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(ClassificationResultsFrameBase.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        rocCurvePanel = new ROCCurvePanel(new RocCurve(evaluation), this, digits);

        pane.add(RESULTS_TEXT, resultPanel);
        pane.add(CLASSIFY_TAB_TITLE, new ClassifyInstancePanel(
                new ClassifyInstanceTable(data, digits), classifier));
        pane.add(ROC_CURVES_TEXT, rocCurvePanel);
        this.add(pane);
    }

    public JFrame getParentFrame() {
        return parent;
    }

    public static void createResults(ClassificationResultsFrameBase resultsFrameBase, int digits) throws Exception {
        if (resultsFrameBase != null) {
            if (resultsFrameBase.classifier() instanceof DecisionTreeClassifier) {
                JScrollPane pane
                        = new JScrollPane(new TreeVisualizer((DecisionTreeClassifier) resultsFrameBase.classifier(),
                        digits));
                resultsFrameBase.addPanel(TREE_STRUCTURE_TAB_TITLE, pane);
                JScrollBar bar = pane.getHorizontalScrollBar();
                bar.setValue(bar.getMaximum());
            } else if (resultsFrameBase.classifier() instanceof NeuralNetwork) {
                NeuralNetwork net = (NeuralNetwork) resultsFrameBase.classifier();
                JScrollPane pane = new JScrollPane(new NetworkVisualizer(net, resultsFrameBase, digits));
                resultsFrameBase.addPanel(NETWORK_STRUCTURE_TAB_TITLE, pane);
            } else if (resultsFrameBase.classifier() instanceof Logistic) {
                LogisticCoefficientsTable table
                        =
                        new LogisticCoefficientsTable((Logistic) resultsFrameBase.classifier(), resultsFrameBase.data(),
                                digits);
                JScrollPane pane = new JScrollPane(table);
                resultsFrameBase.addPanel(LOGISTIC_COEFFICIENTS_TAB_TITLE, pane);

                AttributesSelection attributesSelection = new AttributesSelection(resultsFrameBase.data());
                attributesSelection.calculate();
                SignificantAttributesTable signTable
                        = new SignificantAttributesTable(attributesSelection, digits);
                JScrollPane signPane = new JScrollPane(signTable);

                resultsFrameBase.addPanel(SIGNIFICANT_ATTRIBUTES_TAB_TITLE, signPane);
            } else if (resultsFrameBase.classifier() instanceof EnsembleClassifier) {
                EnsembleTable table = new EnsembleTable((EnsembleClassifier) resultsFrameBase.classifier(),
                        resultsFrameBase.getParentFrame(), digits);
                JScrollPane pane = new JScrollPane(table);
                resultsFrameBase.addPanel(ENSEMBLE_STRUCTURE_TAB_TITLE, pane);
            }
        }
    }

    private String getAttributesInfo() {
        ClassifyInstancePanel classifyInstancePanel = (ClassifyInstancePanel) pane.getComponentAt(1);
        return ClassifierInputOptionsService.getAttributesInfo(data, classifyInstancePanel.getAttributeStatistics());
    }

    /**
     * Class for saving classification results into XLS file.
     */
    private class XlsResultsSaver {

        static final String INPUT_OPTIONS_TEXT = "Входные параметры";
        static final String INDIVIDUAL_CLASSIFIER_INPUT_OPTIONS = "Входные параметры классификатора";
        static final String INDIVIDUAL_CLASSIFIER = "Классификатор";
        static final String CLASSIFIERS_INPUT_OPTIONS = "Входные параметры базовых классификаторов:";
        static final String META_CLASSIFIER_INPUT_OPTIONS = "Входные параметры мета-классификатора";
        static final String META_CLASSIFIER_TEXT = "Мета-классификатор:";
        static final String INDIVIDUAL_CLASSIFIER_TEXT = "Базовый классификатор:";

        void save(File file) throws Exception {
            try (FileOutputStream stream = new FileOutputStream(file)) {
                Workbook book = file.getName().endsWith(DataFileExtension.XLS) ?
                        new HSSFWorkbook() : new XSSFWorkbook();

                Font font = book.createFont();
                font.setBold(true);
                font.setFontHeightInPoints((short) 12);
                CellStyle style = book.createCellStyle();
                style.setFont(font);

                createXlsInputParamSheet(book, style);
                createXlsResultsSheet(book, style);
                writePicture(file, book, (BufferedImage) rocCurvePanel.createImage(), ROC_CURVES_TEXT, 5, 5);

                book.write(stream);
            }
        }

        void createXlsInputParamSheet(Workbook book, CellStyle style) {
            Sheet sheet = book.createSheet(INPUT_OPTIONS_TEXT);
            AbstractClassifier cls = (AbstractClassifier) classifier;

            createTitle(sheet, style, INDIVIDUAL_CLASSIFIER_INPUT_OPTIONS);
            createPair(sheet, style, INDIVIDUAL_CLASSIFIER, cls.getClass().getSimpleName());

            String[] options = cls.getOptions();
            setXlsClassifierOptions(sheet, options);
            if (cls instanceof AbstractHeterogeneousClassifier) {
                createTitle(sheet, style, CLASSIFIERS_INPUT_OPTIONS);
                AbstractHeterogeneousClassifier ens = (AbstractHeterogeneousClassifier) cls;
                ClassifiersSet set = ens.getClassifiersSet();
                setXlsEnsembleOptions(sheet, style, set.toList());
            }
            if (cls instanceof StackingClassifier) {
                createTitle(sheet, style, CLASSIFIERS_INPUT_OPTIONS);
                StackingClassifier ens = (StackingClassifier) cls;
                setXlsEnsembleOptions(sheet, style, ens.getClassifiers().toList());
                createTitle(sheet, style, META_CLASSIFIER_INPUT_OPTIONS);
                String[] metaOptions = ((AbstractClassifier) ens.getMetaClassifier()).getOptions();
                createPair(sheet, style, META_CLASSIFIER_TEXT,
                        ens.getMetaClassifier().getClass().getSimpleName());
                setXlsClassifierOptions(sheet, metaOptions);
            }
        }

        void setXlsClassifierOptions(Sheet sheet, String[] options) {
            for (int i = 0; i < options.length; i += 2) {
                Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                Cell cell = row.createCell(0);
                cell.setCellValue(options[i]);
                sheet.autoSizeColumn(0);
                cell = row.createCell(1);
                cell.setCellValue(options[i + 1]);
                sheet.autoSizeColumn(1);
            }
        }

        void setXlsEnsembleOptions(Sheet sheet, CellStyle style, ArrayList<Classifier> set) {
            for (int i = 0; i < set.size(); i++) {
                AbstractClassifier single = (AbstractClassifier) set.get(i);
                createPair(sheet, style, INDIVIDUAL_CLASSIFIER_TEXT, single.getClass().getSimpleName());
                createTitle(sheet, style, INPUT_OPTIONS_TEXT);
                String[] singleOptions = single.getOptions();
                setXlsClassifierOptions(sheet, singleOptions);
            }
        }

        void createTitle(Sheet sheet, CellStyle style, String title) {
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(title);
            sheet.autoSizeColumn(0);
        }

        void createPair(Sheet sheet, CellStyle style, String title1, String title2) {
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(title1);
            sheet.autoSizeColumn(0);
            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue(title2);
            sheet.autoSizeColumn(1);
        }

        void createXlsResultsSheet(Workbook book, CellStyle style) throws Exception {
            Sheet sheet = book.createSheet(RESULTS_TEXT);
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(STATISTICS_TEXT);
            DecimalFormat fmt = costMatrix.getFormat();
            //------------------------------
            for (int i = 0; i < statTable.getRowCount(); i++) {
                row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                cell = row.createCell(0);
                cell.setCellValue((String) statTable.getValueAt(i, 0));
                cell = row.createCell(1);
                try {
                    cell.setCellValue(fmt.parse((String) statTable.getValueAt(i, 1)).doubleValue());
                } catch (Exception e) {
                    cell.setCellValue((String) statTable.getValueAt(i, 1));
                }
            }
            //------------------------------------------
            row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(RESULTS_TEXT);
            row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            for (int i = 0; i < costMatrix.getColumnCount(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(costMatrix.getColumnName(i));
            }
            //----------------------------
            for (int i = 0; i < costMatrix.getRowCount(); i++) {
                row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                for (int j = 0; j < costMatrix.getColumnCount(); j++) {
                    cell = row.createCell(j);
                    if (j == 0) {
                        cell.setCellValue((Integer) costMatrix.getValueAt(i, j));
                    } else {
                        cell.setCellValue(fmt.parse((String) costMatrix.getValueAt(i, j)).doubleValue());
                    }
                }
            }
            //------------------------------
            row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(MATRIX_TEXT);
            row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            for (int i = 0; i < misMatrix.getColumnCount(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(misMatrix.getColumnName(i));
                sheet.autoSizeColumn(i);
            }
            //-------------------------------------------------
            for (int i = 0; i < misMatrix.getRowCount(); i++) {
                row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                for (int j = 0; j < misMatrix.getColumnCount(); j++) {
                    cell = row.createCell(j);
                    cell.setCellValue((Integer) misMatrix.getValueAt(i, j));
                }
            }
        }

        void writePicture(File file, Workbook book, BufferedImage bImage, String title, int col, int row)
                throws Exception {
            Sheet sheet = book.createSheet(title);
            ByteArrayOutputStream byteArrayImg = new ByteArrayOutputStream();
            ImageIO.write(bImage, "PNG", byteArrayImg);
            int pictureIdx = sheet.getWorkbook().addPicture(
                    byteArrayImg.toByteArray(),
                    sheet.getWorkbook().PICTURE_TYPE_PNG);

            short col1 = 0, col2 = 0;
            ClientAnchor anchor;

            if (file.getName().endsWith(DataFileExtension.XLS)) {
                anchor = new HSSFClientAnchor(0, 0, 0, 0, col1, 0, col2, 0);
            } else {
                anchor = new XSSFClientAnchor(0, 0, 0, 0, col1, 0, col2, 0);
            }

            anchor.setCol1(col);
            anchor.setRow1(row);

            Drawing drawing = sheet.createDrawingPatriarch();
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize();
        }

    }

}
