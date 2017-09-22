/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.Reference;
import eca.client.RestClient;
import eca.client.RestClientImpl;
import eca.config.ApplicationProperties;
import eca.config.EcaServiceProperties;
import eca.core.EvaluationMethod;
import eca.gui.logging.LoggerUtils;
import eca.converters.DataSaver;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationService;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AutomatedNeuralNetwork;
import eca.dataminer.AutomatedStacking;
import eca.dataminer.ClassifiersSetBuilder;
import eca.db.DataBaseConnection;
import eca.ensemble.*;
import eca.ensemble.Iterable;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.gui.ConsoleTextArea;
import eca.gui.service.ExecutorService;
import eca.gui.PanelBorderUtils;
import eca.gui.actions.*;
import eca.gui.choosers.OpenDataFileChooser;
import eca.gui.choosers.OpenModelChooser;
import eca.gui.choosers.SaveDataFileChooser;
import eca.gui.dialogs.*;
import eca.dictionary.ClassifiersNamesDictionary;
import eca.dictionary.EnsemblesNamesDictionary;
import eca.gui.tables.AttributesTable;
import eca.gui.tables.InstancesTable;
import eca.gui.tables.StatisticsTableBuilder;
import eca.text.DateFormat;
import eca.metrics.KNearestNeighbours;
import eca.core.evaluation.EvaluationResults;
import eca.model.InputData;
import eca.model.ModelDescriptor;
import eca.net.DataLoaderImpl;
import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import eca.regression.Logistic;
import eca.trees.*;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * @author Roman Batygin
 */
@Slf4j
public class JMainFrame extends JFrame {

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    private static final EcaServiceProperties ECA_SERVICE_PROPERTIES = EcaServiceProperties.getInstance();

    private static final Color FRAME_COLOR = new Color(198, 226, 255);

    private static final String ENSEMBLE_BUILDING_PROGRESS_TITLE = "Пожалуйста подождите, идет построение ансамбля...";
    private static final String NETWORK_BUILDING_PROGRESS_TITLE =
            "Пожалуйста подождите, идет обучение нейронной сети...";
    private static final String ON_EXIT_TEXT = "Вы уверены, что хотите выйти?";
    private static final String MODEL_BUILDING_MESSAGE = "Пожалуйста подождите, идет построение модели...";
    private static final String DATA_LOADING_MESSAGE = "Пожалуйста подождите, идет загрузка данных...";
    private static final String FILE_MENU_TEXT = "Файл";
    private static final String CLASSIFIERS_MENU_TEXT = "Классификаторы";
    private static final String DATA_MINER_MENU_TEXT = "Data Miner";
    private static final String OPTIONS_MENU_TEXT = "Настройки";
    private static final String SERVICE_MENU_TEXT = "Сервис";
    private static final String WINDOWS_MENU_TEXT = "Окна";
    private static final String REFERENCE_MENU_TEXT = "Справка";
    private static final String OPEN_FILE_MENU_TEXT = "Открыть...";
    private static final String EVALUATION_METHOD_MENU_TEXT = "Настройка метода оценки точности";
    private static final String NUMBER_FORMAT_MENU_TEXT = "Настройка формата чисел";
    private static final String NUMBER_FORMAT_TITLE = "Формат чисел";
    private static final String DECIMAL_PLACES_TITLE = "Количество десятичных знаков:";
    private static final String ECA_SERVICE_MENU_TEXT = "Настройки сервиса ECA";
    private static final String SAVE_FILE_MENU_TEXT = "Сохранить...";
    private static final String DB_CONNECTION_MENU_TEXT = "Подключиться к базе данных";
    private static final String DB_CONNECTION_WAITING_MESSAGE =
            "Пожалуйста подождите, идет подключение к базе данных...";
    private static final String LOAD_MODEL_MENU_TEXT = "Загрузить модель";
    private static final String LOAD_DATA_FROM_NET_MENU_TEXT = "Загрузить данные из сети";
    private static final String URL_FILE_TEXT = "URL файла:";
    private static final String LOAD_DATA_FROM_NET_TITLE = "Загрузка данных из сети";
    private static final String DATA_GENERATION_MENU_TEXT = "Генерация выборки";
    private static final String DATA_GENERATION_LOADING_MESSAGE = "Пожалуйста подождите, идет генерация данных...";
    private static final String EXIT_MENU_TEXT = "Выход";
    private static final String ABOUT_PROGRAM_MENU_TEXT = "О программе";
    private static final String SHOW_REFERENCE_MENU_TEXT = "Показать справку";
    private static final String DATA_MINER_NETWORKS_MENU_TEXT = "Автоматическое построение: нейронные сети";
    private static final String DATA_MINER_HETEROGENEOUS_ENSEMBLE_MENU_TEXT =
            "Автоматическое построение: неоднородный ансамблевый алгоритм";
    private static final String DATA_MINER_MODIFIED_HETEROGENEOUS_ENSEMBLE_MENU_TEXT =
            "Автоматическое построение: модифицированный неоднородный ансамблевый алгоритм";
    private static final String DATA_MINER_ADA_BOOST_MENU_TEXT = "Автоматическое построение: алгоритм AdaBoost";
    private static final String DATA_MINER_STACKING_MENU_TEXT = "Автоматическое построение: алгоритм Stacking";
    private static final String INDIVIDUAL_CLASSIFIERS_MENU_TEXT = "Индувидуальные алгоритмы";
    private static final String ENSEMBLE_CLASSIFIERS_MENU_TEXT = "Ансамблевые алгоритмы";
    private static final String DECISION_TREES_MENU_TEXT = "Деревья решений";
    private static final String CLASSIFIERS_HISTORY_MENU_TEXT = "История классификаторов";
    private static final String ATTRIBUTES_STATISTICS_MENU_TEXT = "Статистика по атрибутам";
    private static final String DEFAULT_URL_FOR_DATA_LOADING = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";
    private static final String EXCEED_DATA_LIST_SIZE_ERROR_FORMAT = "Число листов с данными не должно превышать %d!";
    private static final String CONSOLE_MENU_TEXT = "Открыть консоль";

    private static final double WIDTH_COEFFICIENT = 0.8;
    private static final double HEIGHT_COEFFICIENT = 0.9;

    private final JDesktopPane panels = new JDesktopPane();

    private JMenu algorithmsMenu;

    private JMenu dataMinerMenu;

    private JMenuItem saveFileMenu;

    private JMenuItem attrStatisticsMenu;

    private JMenu windowsMenu;

    private ResultsHistory resultsHistory = new ResultsHistory();

    private int maximumFractionDigits;

    private boolean isStarted;

    private final EvaluationMethodOptionsDialog testingSetFrame = new EvaluationMethodOptionsDialog(this);

    private ClassificationResultHistoryFrame resultHistoryFrame;

    public JMainFrame() {
        Locale.setDefault(Locale.ENGLISH);
        this.init();
        this.makeGUI();
        resultHistoryFrame = new ClassificationResultHistoryFrame(this, resultsHistory);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.algorithmsMenu.setEnabled(false);
        this.saveFileMenu.setEnabled(false);
        this.attrStatisticsMenu.setEnabled(false);
        this.dataMinerMenu.setEnabled(false);
        this.createWindowListener();
        this.setLocationRelativeTo(null);
    }

    private void init() {
        try {
            this.setTitle(APPLICATION_PROPERTIES.getTitle());
            this.maximumFractionDigits = APPLICATION_PROPERTIES.getDefaultFractionDigits();
            this.setIconImage(ImageIO.read(getClass().getClassLoader()
                    .getResource(APPLICATION_PROPERTIES.getIconUrl())));
            ToolTipManager.sharedInstance().setDismissDelay(APPLICATION_PROPERTIES.getTooltipDismissTime());
        } catch (Exception e) {
            LoggerUtils.error(log, e);
        }
    }

    private void closeWindow() {
        if (isStarted) {
            int result = JOptionPane.showConfirmDialog(JMainFrame.this,
                    ON_EXIT_TEXT, null,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                JMainFrame.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                System.exit(0);
            }
        } else {
            JMainFrame.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            System.exit(0);
        }
    }

    private void createWindowListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                JMainFrame.this.closeWindow();
            }
        });
    }

    /**
     *
     */
    private class DataInternalFrame extends JInternalFrame {

        static final String UPPER_TITLE = "Информация о данных";
        static final String DATA_TITLE = "Таблица с данными";
        static final String ATTR_TITLE = "Выбранные атрибуты";
        static final String CLASS_TITLE = "Выбранный класс";
        static final String DATA_CHANGE_NAME_MENU_TEXT = "Изменение названия данных";
        static final String CHOOSE_COLOR_MENU_TEXT = "Выбор цвета фона";
        static final String SAVE_DATA_MENU_TEXT = "Сохранить...";
        static final String DATA_NAME_TEXT = "Название:";
        static final String NEW_DATA_NAME_TEXT = "Новое название данных";
        static final String NUMBER_OF_INSTANCES_TEXT = "Число объектов: ";
        static final String NUMBER_OF_ATTRIBUTES_TEXT = "Число атрибутов: ";
        static final String CHOOSE_ALL_ATTRIBUTES_BUTTON_TEXT = "Выбрать все";
        static final String RESET_ALL_ATTRIBUTES_BUTTON_TEXT = "Сброс";

        final Instances data;

        JPanel upperPanel;
        JPanel lowerPanel;
        JTextField relationName;
        JTextField numInstances;
        JTextField numAttributes;

        JScrollPane dataScrollPane;
        JScrollPane attrScrollPane;
        JPanel attrPanel;

        JComboBox<String> classBox;
        InstancesTable instanceTable;
        AttributesTable attributesTable;
        JButton selectButton;
        JButton resetButton;

        JMenuItem menu;

        public DataInternalFrame(Instances data, JMenuItem menu) throws Exception {
            this.setLayout(new GridBagLayout());
            this.makeUpperPanel();
            this.makeLowerPanel();
            this.setFrameColor(FRAME_COLOR);
            this.data = data;
            this.setMenu(menu);
            this.createPopMenu();
            this.setRelationInfo();
            this.convertDataToTables();
            this.setClosable(true);
            this.setResizable(true);
            this.setMaximizable(true);
            this.pack();
        }

        public final void setMenu(JMenuItem menu) {
            this.menu = menu;
        }

        public final JMenuItem getMenu() {
            return menu;
        }

        public Instances getData() throws Exception {
            return attributesTable.createData();
        }

        public void check() throws Exception {
            attributesTable.check();
        }

        public final void setFrameColor(Color color) {
            this.setBackground(color);
            upperPanel.setBackground(color);
            lowerPanel.setBackground(color);
            attrPanel.setBackground(color);
            dataScrollPane.setBackground(color);
            attrScrollPane.setBackground(color);
        }

        private void createPopMenu() {
            JPopupMenu popMenu = new JPopupMenu();
            JMenuItem nameMenu = new JMenuItem(DATA_CHANGE_NAME_MENU_TEXT);
            JMenuItem colorMenu = new JMenuItem(CHOOSE_COLOR_MENU_TEXT);
            JMenuItem saveMenu = new JMenuItem(SAVE_DATA_MENU_TEXT);
            //-----------------------------------
            nameMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    String name = (String) JOptionPane.showInputDialog(DataInternalFrame.this,
                            DATA_NAME_TEXT, NEW_DATA_NAME_TEXT, JOptionPane.INFORMATION_MESSAGE, null,
                            null, data.relationName());
                    if (name != null) {
                        String trimName = name.trim();
                        if (!trimName.isEmpty()) {
                            instanceTable.data().setRelationName(trimName);
                            relationName.setText(trimName);
                            menu.setText(trimName);
                        }
                    }
                }
            });
            //-----------------------------------
            colorMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Color color = JColorChooser.showDialog(DataInternalFrame.this, colorMenu.getText(),
                            getBackground());
                    if (color != null) {
                        setFrameColor(color);
                    }
                }
            });
            //-----------------------------------
            saveMenu.addActionListener(new ActionListener() {

                SaveDataFileChooser fileChooser;

                @Override
                public void actionPerformed(ActionEvent evt) {
                    try {
                        if (dataValidated()) {
                            if (fileChooser == null) {
                                fileChooser = new SaveDataFileChooser();
                            }
                            fileChooser.setSelectedFile(new File(instanceTable.data().relationName()));
                            File file = fileChooser.getSelectedFile(DataInternalFrame.this);
                            if (file != null) {
                                DataSaver.saveData(file, DataInternalFrame.this.getData());
                            }
                        }
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(DataInternalFrame.this, e.getMessage(),
                                null, JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            //-----------------------------------
            popMenu.add(nameMenu);
            popMenu.add(colorMenu);
            popMenu.addSeparator();
            popMenu.add(saveMenu);
            this.setComponentPopupMenu(popMenu);
        }

        private void setRelationInfo() {
            relationName.setText(data.relationName());
            numInstances.setText(String.valueOf(data.numInstances()));
            numAttributes.setText(String.valueOf(data.numAttributes()));
        }

        private void makeUpperPanel() {
            upperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            upperPanel.setBorder(PanelBorderUtils.createTitledBorder(UPPER_TITLE));
            //----------------------------------------------
            relationName = new JTextField(30);
            relationName.setEditable(false);
            relationName.setBackground(Color.WHITE);
            numInstances = new JTextField(6);
            numInstances.setEditable(false);
            numInstances.setBackground(Color.WHITE);
            numAttributes = new JTextField(4);
            numAttributes.setEditable(false);
            numAttributes.setBackground(Color.WHITE);
            upperPanel.add(new JLabel(DATA_NAME_TEXT));
            upperPanel.add(relationName);
            upperPanel.add(new JLabel(NUMBER_OF_INSTANCES_TEXT));
            upperPanel.add(numInstances);
            upperPanel.add(new JLabel(NUMBER_OF_ATTRIBUTES_TEXT));
            upperPanel.add(numAttributes);
            //----------------------------------------------
            this.add(upperPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(5, 0, 5, 0), 0, 0));
            //----------------------------------------------
        }

        private void makeLowerPanel() {
            lowerPanel = new JPanel(new GridBagLayout());
            //--------------------------------------------
            dataScrollPane = new JScrollPane();
            dataScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setBorder(PanelBorderUtils.createTitledBorder(DATA_TITLE));
            //-------------------------------------------
            this.makeAttrPanel();
            //------------------------------------------
            lowerPanel.add(dataScrollPane, new GridBagConstraints(0, 0, 1, 2, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 2, 5), 0, 0));
            lowerPanel.add(attrPanel, new GridBagConstraints(1, 0, 1, 1, 0, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 2, 0), 0, 0));
            //---------------------------------------------
            this.add(lowerPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }

        private void makeAttrPanel() {
            attrPanel = new JPanel(new GridBagLayout());
            attrPanel.setBorder(PanelBorderUtils.createTitledBorder(ATTR_TITLE));
            selectButton = new JButton(CHOOSE_ALL_ATTRIBUTES_BUTTON_TEXT);
            resetButton = new JButton(RESET_ALL_ATTRIBUTES_BUTTON_TEXT);
            //-------------------------------------------
            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    attributesTable.selectAllAttributes();
                }
            });
            //-------------------------------------------
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    attributesTable.resetValues();
                }
            });
            //-------------------------------------------
            attrScrollPane = new JScrollPane();
            attrScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            attrScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            double width = 0.35 * WIDTH_COEFFICIENT * Toolkit.getDefaultToolkit().getScreenSize().width;
            attrScrollPane.setPreferredSize(new Dimension((int) width,
                    400));
            //-------------------------------------------
            classBox = new JComboBox<>();
            classBox.setBorder(PanelBorderUtils.createTitledBorder(CLASS_TITLE));
            Dimension dim = new Dimension((int) width, 50);
            classBox.setPreferredSize(dim);
            classBox.setMinimumSize(dim);
            classBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    data.setClassIndex(classBox.getSelectedIndex());
                }
            });
            //------------------------------------------
            attrPanel.add(selectButton, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 5, 2), 0, 0));
            attrPanel.add(resetButton, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 2, 5, 0), 0, 0));
            attrPanel.add(attrScrollPane, new GridBagConstraints(0, 1, 2, 1, 0, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
            attrPanel.add(classBox, new GridBagConstraints(0, 2, 2, 1, 0, 0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 2, 0), 0, 0));
        }

        private void convertDataToTables() {
            for (int i = 0; i < data.numAttributes(); i++) {
                classBox.addItem(data.attribute(i).name());
            }
            data.setClassIndex(data.numAttributes() - 1);
            classBox.setSelectedIndex(data.classIndex());
            instanceTable = new InstancesTable(data, numInstances);
            dataScrollPane.setViewportView(instanceTable);
            attributesTable = new AttributesTable(instanceTable, classBox);
            attrScrollPane.setViewportView(attributesTable);
            dataScrollPane.setComponentPopupMenu(instanceTable.getComponentPopupMenu());
        }

    } //End of class DataInternalFrame

    /**
     *
     */
    private class ModelBuilder implements CallbackAction {

        Classifier model;
        Instances data;
        Evaluation evaluation;

        ModelBuilder(Classifier model, Instances data) {
            this.model = model;
            this.data = data;
        }

        @Override
        public void apply() throws Exception {
            evaluation = EvaluationService.evaluateModel(model, data, testingSetFrame.getEvaluationMethod(),
                    testingSetFrame.numFolds(), testingSetFrame.numTests(), new Random());
        }

        public Evaluation evaluation() {
            return evaluation;
        }

    } //End of class ModelBuilder

    /**
     *
     */
    private class EcaServiceAction implements CallbackAction {

        RestClient restClient;
        InputData inputData;
        EvaluationResults classifierDescriptor;

        EcaServiceAction(RestClient restClient, InputData inputData) {
            this.restClient = restClient;
            this.inputData = inputData;
        }

        @Override
        public void apply() throws Exception {
            classifierDescriptor = restClient.performRequest(inputData);
        }

        EvaluationResults getClassifierDescriptor() {
            return classifierDescriptor;
        }
    }

    /**
     *
     */
    public class ResultsHistory extends DefaultListModel<String> {

        private static final String HISTORY_FORMAT = "%s %s";

        private ArrayList<ClassificationResultsFrameBase> resultsFrameBases = new ArrayList<>();

        public void createResultFrame(String title,
                                      Classifier classifier,
                                      Instances data,
                                      Evaluation evaluation,
                                      int digits) throws Exception {
            ClassificationResultsFrameBase res = new ClassificationResultsFrameBase(JMainFrame.this,
                    title, classifier, data, evaluation,
                    digits);
            StatisticsTableBuilder stat = new StatisticsTableBuilder(digits);
            res.setStatisticsTable(stat.createStatistics(classifier, evaluation));
            ClassificationResultsFrameBase.createResults(res, digits);
            add(res);
            res.setVisible(true);

            log.info("Evaluation for classifier {} has been successfully finished!",
                    classifier.getClass().getSimpleName());
        }

        public void add(ClassificationResultsFrameBase resultsFrameBase) {
            resultsFrameBases.add(resultsFrameBase);
            addElement(String.format(HISTORY_FORMAT,
                    DateFormat.SIMPLE_DATE_FORMAT.format(resultsFrameBase.getIndexer().getCurrentDate()),
                    resultsFrameBase.classifier().getClass().getSimpleName()));
        }

        public ArrayList<ClassificationResultsFrameBase> getResultsFrameBases() {
            return resultsFrameBases;
        }

        public ClassificationResultsFrameBase getFrame(int i) {
            return resultsFrameBases.get(i);
        }

    }

    private void executeWithEcaService(final BaseOptionsDialog frame) throws Exception {
        InputData inputData = new InputData((AbstractClassifier) frame.classifier(),
                frame.data());

        RestClientImpl restClient = new RestClientImpl();
        restClient.setEvaluationMethod(testingSetFrame.getEvaluationMethod());

        if (restClient.getEvaluationMethod() == EvaluationMethod.CROSS_VALIDATION) {
            restClient.setNumFolds(testingSetFrame.numFolds());
            restClient.setNumTests(testingSetFrame.numTests());
        }

        EcaServiceAction ecaServiceAction = new EcaServiceAction(restClient, inputData);

        LoadDialog progress = new LoadDialog(JMainFrame.this,
                ecaServiceAction, MODEL_BUILDING_MESSAGE);

        process(progress, new CallbackAction() {
            @Override
            public void apply() throws Exception {
                EvaluationResults classifierDescriptor = ecaServiceAction.getClassifierDescriptor();
                resultsHistory.createResultFrame(frame.getTitle(), classifierDescriptor.getClassifier(),
                        frame.data(), classifierDescriptor.getEvaluation(), maximumFractionDigits);
            }
        });
    }

    public void process(ExecutorDialog executorDialog, CallbackAction successAction) throws Exception {
        ExecutorService.process(executorDialog, successAction, new CallbackAction() {
            @Override
            public void apply() {
                JOptionPane.showMessageDialog(JMainFrame.this,
                        executorDialog.getErrorMessageText(), null, JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void executeSimpleBuilding(BaseOptionsDialog frame) throws Exception {
        frame.showDialog();
        if (frame.dialogResult()) {

            List<String> options = Arrays.asList(((AbstractClassifier) frame.classifier()).getOptions());

            log.info("Starting evaluation for classifier {} with options: {} on data '{}'",
                    frame.classifier().getClass().getSimpleName(), options, frame.data().relationName());

            if (ECA_SERVICE_PROPERTIES.getEcaServiceEnabled()) {
                executeWithEcaService(frame);
            } else {
                ModelBuilder builder = new ModelBuilder(frame.classifier(), frame.data());
                LoadDialog progress = new LoadDialog(JMainFrame.this,
                        builder, MODEL_BUILDING_MESSAGE);

                process(progress, new CallbackAction() {
                    @Override
                    public void apply() throws Exception {
                        resultsHistory.createResultFrame(frame.getTitle(), frame.classifier(), frame.data(),
                                builder.evaluation(), maximumFractionDigits);

                    }
                });
            }

        }
        frame.dispose();
    }


    private DataInternalFrame selectedPanel() {
        return (DataInternalFrame) panels.getSelectedFrame();
    }

    private Instances data() throws Exception {
        return selectedPanel().getData();
    }

    private void makeGUI() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (WIDTH_COEFFICIENT * dim.width), (int) (HEIGHT_COEFFICIENT * dim.height));
        this.makeMenu();
        panels.setBackground(Color.GRAY);
        this.add(panels);
    }

    public void createDataFrame(Instances data) throws Exception {
        if (panels.getComponentCount() >= APPLICATION_PROPERTIES.getMaximumListSizeOfData()) {
            throw new Exception(String.format(EXCEED_DATA_LIST_SIZE_ERROR_FORMAT,
                    APPLICATION_PROPERTIES.getMaximumListSizeOfData()));
        }
        final DataInternalFrame dataInternalFrame =
                new DataInternalFrame(data, new JCheckBoxMenuItem(data.relationName()));

        dataInternalFrame.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                windowsMenu.remove(dataInternalFrame.getMenu());
                if (panels.getComponentCount() == 0) {
                    algorithmsMenu.setEnabled(false);
                    saveFileMenu.setEnabled(false);
                    dataMinerMenu.setEnabled(false);
                    attrStatisticsMenu.setEnabled(false);
                }
            }

            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                dataInternalFrame.getMenu().setSelected(true);
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                dataInternalFrame.getMenu().setSelected(false);
            }
        });
        //-----------------------------
        dataInternalFrame.getMenu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    dataInternalFrame.setSelected(true);
                    dataInternalFrame.getMenu().setSelected(true);
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                }
            }
        });
        //--------------------------------------------
        panels.add(dataInternalFrame);
        dataInternalFrame.setVisible(true);
        algorithmsMenu.setEnabled(true);
        saveFileMenu.setEnabled(true);
        dataMinerMenu.setEnabled(true);
        attrStatisticsMenu.setEnabled(true);
        windowsMenu.add(dataInternalFrame.getMenu());
        isStarted = true;
    }

    private void makeMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        algorithmsMenu = new JMenu(CLASSIFIERS_MENU_TEXT);
        dataMinerMenu = new JMenu(DATA_MINER_MENU_TEXT);
        JMenu optionsMenu = new JMenu(OPTIONS_MENU_TEXT);
        JMenu serviceMenu = new JMenu(SERVICE_MENU_TEXT);
        windowsMenu = new JMenu(WINDOWS_MENU_TEXT);
        JMenu referenceMenu = new JMenu(REFERENCE_MENU_TEXT);
        menu.add(fileMenu);
        menu.add(algorithmsMenu);
        menu.add(dataMinerMenu);
        menu.add(optionsMenu);
        menu.add(serviceMenu);
        menu.add(windowsMenu);
        menu.add(referenceMenu);
        //-------------------------------
        JMenuItem openFileMenu = new JMenuItem(OPEN_FILE_MENU_TEXT);
        openFileMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        //-------------------------------
        openFileMenu.addActionListener(new ActionListener() {

            OpenDataFileChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new OpenDataFileChooser();
                    }
                    File file = fileChooser.openFile(JMainFrame.this);
                    if (file != null) {
                        InstancesLoader loader = new InstancesLoader(file);
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader,
                                DATA_LOADING_MESSAGE);

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                createDataFrame(loader.data());
                            }
                        });
                        //---------------------------------------
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //------------------------------------------------
        JMenuItem sampleMenu = new JMenuItem(EVALUATION_METHOD_MENU_TEXT);
        sampleMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                testingSetFrame.showDialog();
            }
        });
        optionsMenu.add(sampleMenu);
        //------------------------------------------------
        JMenuItem digitsMenu = new JMenuItem(NUMBER_FORMAT_MENU_TEXT);
        digitsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                SpinnerDialog dialog = new SpinnerDialog(JMainFrame.this,
                        NUMBER_FORMAT_TITLE, DECIMAL_PLACES_TITLE, maximumFractionDigits,
                        APPLICATION_PROPERTIES.getMinimumFractionDigits(),
                        APPLICATION_PROPERTIES.getMaximumFractionDigits());
                dialog.setVisible(true);
                if (dialog.dialogResult()) {
                    maximumFractionDigits = dialog.getValue();
                }
                dialog.dispose();
            }
        });
        optionsMenu.add(digitsMenu);
        //-------------------------------

        JMenuItem ecaServiceOptionsMenu = new JMenuItem(ECA_SERVICE_MENU_TEXT);
        ecaServiceOptionsMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                EcaServiceOptionsDialog ecaServiceOptionsDialog = new EcaServiceOptionsDialog(JMainFrame.this);
                ecaServiceOptionsDialog.setVisible(true);
            }
        });
        optionsMenu.add(ecaServiceOptionsMenu);

        fileMenu.add(openFileMenu);
        saveFileMenu = new JMenuItem(SAVE_FILE_MENU_TEXT);
        saveFileMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        fileMenu.add(saveFileMenu);
        //-------------------------------------------------
        saveFileMenu.addActionListener(new ActionListener() {

            SaveDataFileChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (dataValidated()) {
                        if (fileChooser == null) {
                            fileChooser = new SaveDataFileChooser();
                        }
                        fileChooser.setSelectedFile(new File(data().relationName()));
                        File file = fileChooser.getSelectedFile(JMainFrame.this);
                        if (file != null) {
                            DataSaver.saveData(file, data());
                        }
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //---------------------------------------------
        JMenuItem dbMenu = new JMenuItem(DB_CONNECTION_MENU_TEXT);
        dbMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift D"));
        fileMenu.addSeparator();
        fileMenu.add(dbMenu);
        dbMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DatabaseConnectionDialog conn = new DatabaseConnectionDialog(JMainFrame.this);
                conn.setVisible(true);
                if (conn.dialogResult()) {
                    try {
                        DataBaseConnection connection = new DataBaseConnection();
                        connection.setConnectionDescriptor(conn.getConnectionDescriptor());
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                new DataBaseConnectionAction(connection),
                                DB_CONNECTION_WAITING_MESSAGE);

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                QueryFrame queryFrame = new QueryFrame(JMainFrame.this, connection);
                                queryFrame.setVisible(true);
                            }
                        });

                    } catch (Throwable e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
                conn.dispose();
            }
        });
        //---------------------------------------------
        JMenuItem urlMenu = new JMenuItem(LOAD_DATA_FROM_NET_MENU_TEXT);
        urlMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift N"));
        fileMenu.addSeparator();
        fileMenu.add(urlMenu);
        urlMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                String path = (String) JOptionPane.showInputDialog(JMainFrame.this,
                        URL_FILE_TEXT, LOAD_DATA_FROM_NET_TITLE, JOptionPane.INFORMATION_MESSAGE, null,
                        null, DEFAULT_URL_FOR_DATA_LOADING);

                if (path != null) {
                    try {
                        URLLoader loader = new URLLoader(new DataLoaderImpl(new URL(path.trim())));
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader, DATA_LOADING_MESSAGE);

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                createDataFrame(loader.data());
                            }
                        });

                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                                null, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //------------------------------------------------------
        JMenuItem loadModelMenu = new JMenuItem(LOAD_MODEL_MENU_TEXT);
        loadModelMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl M"));
        fileMenu.addSeparator();
        fileMenu.add(loadModelMenu);
        loadModelMenu.addActionListener(new ActionListener() {

            OpenModelChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new OpenModelChooser();
                    }
                    File file = fileChooser.openFile(JMainFrame.this);
                    if (file != null) {
                        ModelLoader loader = new ModelLoader(file);
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader,
                                MODEL_BUILDING_MESSAGE);

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                ModelDescriptor model = loader.model();
                                resultsHistory.createResultFrame(model.getDescription(),
                                        model.getInputData().getClassifier(),
                                        model.getInputData().getData(),
                                        model.getEvaluation(), model.getDigits());
                            }
                        });

                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem generatorMenu = new JMenuItem(DATA_GENERATION_MENU_TEXT);
        generatorMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift G"));
        fileMenu.addSeparator();
        fileMenu.add(generatorMenu);
        generatorMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DataGeneratorDialog dialog = new DataGeneratorDialog(JMainFrame.this);
                dialog.setVisible(true);
                if (dialog.dialogResult()) {
                    try {
                        DataGeneratorLoader loader = new DataGeneratorLoader(dialog.getDataGenerator());
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader,
                                DATA_GENERATION_LOADING_MESSAGE);

                        process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                createDataFrame(loader.getResult());
                            }
                        });

                    } catch (Throwable ex) {
                        LoggerUtils.error(log, ex);
                        JOptionPane.showMessageDialog(JMainFrame.this, ex.getMessage(),
                                null, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        //---------------------------------------------
        JMenuItem exitMenu = new JMenuItem(EXIT_MENU_TEXT);
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);
        //-------------------------------------------------
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JMainFrame.this.closeWindow();
            }
        });
        //-------------------------------
        JMenuItem aboutProgramMenu = new JMenuItem(ABOUT_PROGRAM_MENU_TEXT);
        JMenuItem reference = new JMenuItem(SHOW_REFERENCE_MENU_TEXT);
        reference.setAccelerator(KeyStroke.getKeyStroke("F1"));
        //-------------------------------------------------
        aboutProgramMenu.addActionListener(new ActionListener() {

            AboutProgramFrame frame;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (frame == null) {
                    frame = new AboutProgramFrame(JMainFrame.this);
                }
                frame.setVisible(true);
            }
        });
        //-------------------------------------------------
        reference.addActionListener(new ActionListener() {

            Reference ref;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (ref == null) {
                        ref = new Reference();
                    }
                    ref.openReference();
                } catch (Throwable e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //-----------------------------------------------
        referenceMenu.add(aboutProgramMenu);
        referenceMenu.add(reference);
        //---------------------------------------------------
        JMenuItem aNeuralMenu = new JMenuItem(DATA_MINER_NETWORKS_MENU_TEXT);
        JMenuItem aHeteroEnsMenu = new JMenuItem(DATA_MINER_HETEROGENEOUS_ENSEMBLE_MENU_TEXT);
        JMenuItem modifiedHeteroEnsMenu =
                new JMenuItem(DATA_MINER_MODIFIED_HETEROGENEOUS_ENSEMBLE_MENU_TEXT);
        JMenuItem aAdaBoostMenu = new JMenuItem(DATA_MINER_ADA_BOOST_MENU_TEXT);
        JMenuItem aStackingMenu = new JMenuItem(DATA_MINER_STACKING_MENU_TEXT);
        //--------------------------------------------------
        aNeuralMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances data = data();
                        AutomatedNeuralNetwork net =
                                new AutomatedNeuralNetwork(NeuralNetworkUtil.getActivationFunctions(),
                                        data, new NeuralNetwork(data));
                        ExperimentFrame experimentFrame =
                                new AutomatedNeuralNetworkFrame(net, JMainFrame.this, maximumFractionDigits);
                        experimentFrame.setVisible(true);
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        modifiedHeteroEnsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createEnsembleExperiment(new ModifiedHeterogeneousClassifier(), modifiedHeteroEnsMenu.getText(),
                                data());
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        aHeteroEnsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createEnsembleExperiment(new HeterogeneousClassifier(), aHeteroEnsMenu.getText(), data());
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        aAdaBoostMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createEnsembleExperiment(new AdaBoostClassifier(), aAdaBoostMenu.getText(), data());
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //--------------------------------------------------
        aStackingMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        createStackingExperiment(new StackingClassifier(), aStackingMenu.getText(), data());
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //----------------------------------------
        dataMinerMenu.add(aNeuralMenu);
        dataMinerMenu.add(aHeteroEnsMenu);
        dataMinerMenu.add(modifiedHeteroEnsMenu);
        dataMinerMenu.add(aAdaBoostMenu);
        dataMinerMenu.add(aStackingMenu);
        //-------------------------------
        JMenu classifiersMenu = new JMenu(INDIVIDUAL_CLASSIFIERS_MENU_TEXT);
        JMenu ensembleMenu = new JMenu(ENSEMBLE_CLASSIFIERS_MENU_TEXT);
        algorithmsMenu.add(classifiersMenu);
        algorithmsMenu.add(ensembleMenu);
        //------------------------------
        JMenu treesMenu = new JMenu(DECISION_TREES_MENU_TEXT);
        classifiersMenu.add(treesMenu);
        JMenuItem id3Item = new JMenuItem(ClassifiersNamesDictionary.ID3);
        JMenuItem c45Item = new JMenuItem(ClassifiersNamesDictionary.C45);
        JMenuItem cartItem = new JMenuItem(ClassifiersNamesDictionary.CART);
        JMenuItem chaidItem = new JMenuItem(ClassifiersNamesDictionary.CHAID);
        id3Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    ID3 id3 = new ID3();
                    id3.setUseBinarySplits(false);
                    createTreeOptionDialog(ClassifiersNamesDictionary.ID3, id3);
                }
            }
        });
        c45Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    C45 c45 = new C45();
                    c45.setUseBinarySplits(false);
                    createTreeOptionDialog(ClassifiersNamesDictionary.C45, c45);
                }
            }
        });
        cartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createTreeOptionDialog(ClassifiersNamesDictionary.CART, new CART());
                }
            }
        });
        chaidItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createTreeOptionDialog(ClassifiersNamesDictionary.CHAID, new CHAID());
                }
            }
        });

        treesMenu.add(id3Item);
        treesMenu.add(c45Item);
        treesMenu.add(cartItem);
        treesMenu.add(chaidItem);
        //------------------------------------------------------------------
        JMenuItem logisticItem = new JMenuItem(ClassifiersNamesDictionary.LOGISTIC);
        classifiersMenu.add(logisticItem);
        logisticItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances set = data();
                        LogisticOptionsDialogBase frame = new LogisticOptionsDialogBase(JMainFrame.this,
                                ClassifiersNamesDictionary.LOGISTIC, new Logistic(), set);
                        executeSimpleBuilding(frame);
                    } catch (Throwable e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        });
        //------------------------------------------------------------------
        JMenuItem mlpItem = new JMenuItem(ClassifiersNamesDictionary.NEURAL_NETWORK);
        classifiersMenu.add(mlpItem);
        mlpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances set = data();
                        NetworkOptionsDialog frame = new NetworkOptionsDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.NEURAL_NETWORK, new NeuralNetwork(set), set);
                        frame.showDialog();
                        executeIterativeBuilding(frame, NETWORK_BUILDING_PROGRESS_TITLE);
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //------------------------------------------------------------------
        JMenuItem knnItem = new JMenuItem(ClassifiersNamesDictionary.KNN);
        classifiersMenu.add(knnItem);
        knnItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        KNNOptionDialog frame = new KNNOptionDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.KNN, new KNearestNeighbours(), data());
                        executeSimpleBuilding(frame);
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(),
                                null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //----------------------------------
        JMenuItem heterogeneousItem = new JMenuItem(EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE);
        heterogeneousItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createEnsembleOptionDialog(EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE,
                            new HeterogeneousClassifier(), true);
                }
            }
        });
        ensembleMenu.add(heterogeneousItem);
        //----------------------------------
        JMenuItem boostingItem = new JMenuItem(EnsemblesNamesDictionary.BOOSTING);
        boostingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createEnsembleOptionDialog(EnsemblesNamesDictionary.BOOSTING,
                            new AdaBoostClassifier(), false);
                }
            }
        });
        //----------------------------------
        JMenuItem rndSubSpaceItem = new JMenuItem(EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE);
        rndSubSpaceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    createEnsembleOptionDialog(EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE,
                            new ModifiedHeterogeneousClassifier(), true);
                }
            }
        });
        ensembleMenu.add(rndSubSpaceItem);
        ensembleMenu.add(boostingItem);
        //----------------------------------
        JMenuItem rndForestsItem = new JMenuItem(EnsemblesNamesDictionary.RANDOM_FORESTS);
        rndForestsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances data = data();
                        RandomForestsOptionDialog frame =
                                new RandomForestsOptionDialog(JMainFrame.this, EnsemblesNamesDictionary.RANDOM_FORESTS,
                                        new RandomForests(data), data);
                        frame.showDialog();
                        executeIterativeBuilding(frame, ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        ensembleMenu.add(rndForestsItem);

        JMenuItem extraTreesItem = new JMenuItem(EnsemblesNamesDictionary.EXTRA_TREES);
        extraTreesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        Instances data = data();
                        RandomForestsOptionDialog frame = new RandomForestsOptionDialog(JMainFrame.this,
                                EnsemblesNamesDictionary.EXTRA_TREES, new ExtraTreesClassifier(data), data);
                        frame.showDialog();
                        executeIterativeBuilding(frame, ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        ensembleMenu.add(extraTreesItem);

        //-------------------------------------------------
        JMenuItem stackingItem = new JMenuItem(EnsemblesNamesDictionary.STACKING);
        stackingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        StackingOptionsDialog frame = new StackingOptionsDialog(JMainFrame.this,
                                EnsemblesNamesDictionary.STACKING, new StackingClassifier(), data());
                        executeSimpleBuilding(frame);
                    } catch (Throwable e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        ensembleMenu.add(stackingItem);

        //----------------------------------
        JMenuItem rndNetworksItem = new JMenuItem(EnsemblesNamesDictionary.RANDOM_NETWORKS);
        rndNetworksItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {

                        Instances data = data();
                        RandomNetworkOptionsDialog networkOptionsDialog =
                                new RandomNetworkOptionsDialog(JMainFrame.this,
                                        EnsemblesNamesDictionary.RANDOM_NETWORKS, new RandomNetworks(), data);
                        networkOptionsDialog.showDialog();
                        executeIterativeBuilding(networkOptionsDialog, ENSEMBLE_BUILDING_PROGRESS_TITLE);

                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        ensembleMenu.add(rndNetworksItem);

        //-------------------------------------------------------------------
        JMenuItem historyMenu = new JMenuItem(CLASSIFIERS_HISTORY_MENU_TEXT);
        historyMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                resultHistoryFrame.setVisible(true);
            }
        });
        serviceMenu.add(historyMenu);

        attrStatisticsMenu = new JMenuItem(ATTRIBUTES_STATISTICS_MENU_TEXT);
        attrStatisticsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        AttributesStatisticsFrame frame =
                                new AttributesStatisticsFrame(data(), JMainFrame.this, maximumFractionDigits);
                        frame.setVisible(true);
                    } catch (Throwable ex) {
                        LoggerUtils.error(log, ex);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                ex.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        serviceMenu.add(attrStatisticsMenu);

        JMenuItem loggingMenu = new JMenuItem(CONSOLE_MENU_TEXT);
        loggingMenu.addActionListener(new ActionListener() {

            ConsoleFrame consoleFrame = new ConsoleFrame(JMainFrame.this,
                    ConsoleTextArea.getTextArea());
            ;

            @Override
            public void actionPerformed(ActionEvent evt) {
                consoleFrame.setVisible(true);
            }
        });
        serviceMenu.add(loggingMenu);

        this.setJMenuBar(menu);
    }

    private void executeIterativeBuilding(BaseOptionsDialog frame, String progressMessage) throws Exception {
        if (frame.dialogResult()) {

            List<String> options = Arrays.asList(((AbstractClassifier) frame.classifier()).getOptions());

            log.info("Starting evaluation for classifier {} with options: {} on data '{}'",
                    frame.classifier().getClass().getSimpleName(), options, frame.data().relationName());

            try {

                if (ECA_SERVICE_PROPERTIES.getEcaServiceEnabled()) {
                    executeWithEcaService(frame);
                } else {

                    IterativeBuilder iterativeBuilder = createIterativeClassifier((Iterable) frame.classifier(),
                            frame.data());

                    ClassifierBuilderDialog progress
                            = new ClassifierBuilderDialog(JMainFrame.this, iterativeBuilder, progressMessage);

                    process(progress, new CallbackAction() {
                        @Override
                        public void apply() throws Exception {
                            resultsHistory.createResultFrame(frame.getTitle(), frame.classifier(),
                                    frame.data(), iterativeBuilder.evaluation(), maximumFractionDigits);

                        }
                    });
                }
            } catch (Throwable e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(JMainFrame.this,
                        e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
            }

        }
    }

    private boolean dataValidated() {
        try {
            selectedPanel().check();
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(),
                    null, JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void createTreeOptionDialog(String title, DecisionTreeClassifier tree) {
        try {
            DecisionTreeOptionsDialog frame
                    = new DecisionTreeOptionsDialog(JMainFrame.this, title,
                    tree, data());
            executeSimpleBuilding(frame);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(),
                    null, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createEnsembleOptionDialog(String title, AbstractHeterogeneousClassifier ens,
                                            boolean sample) {
        try {
            EnsembleOptionsDialog frame
                    = new EnsembleOptionsDialog(JMainFrame.this, title, ens,
                    data());
            frame.setSampleEnabled(sample);
            frame.showDialog();
            executeIterativeBuilding(frame, ENSEMBLE_BUILDING_PROGRESS_TITLE);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
        }
    }

    private IterativeBuilder createIterativeClassifier(final Iterable model, final Instances data)
            throws Exception {
        switch (testingSetFrame.getEvaluationMethod()) {
            case TRAINING_DATA: {
                return model.getIterativeBuilder(data);
            }

            case CROSS_VALIDATION: {
                return new CVIterativeBuilder(model, data, testingSetFrame.numFolds(), testingSetFrame.numTests());
            }
        }
        return null;
    }

    private void createEnsembleExperiment(AbstractHeterogeneousClassifier classifier,
                                          String title, Instances data) throws Exception {
        classifier.setClassifiersSet(ClassifiersSetBuilder.createClassifiersSet(data));
        AutomatedHeterogeneousEnsemble exp = new AutomatedHeterogeneousEnsemble(classifier, data);
        AutomatedHeterogeneousEnsembleFrame frame
                = new AutomatedHeterogeneousEnsembleFrame(title, exp, this, maximumFractionDigits);
        frame.setVisible(true);
    }

    private void createStackingExperiment(StackingClassifier classifier,
                                          String title, Instances data) throws Exception {
        classifier.setClassifiers(ClassifiersSetBuilder.createClassifiersSet(data));
        AutomatedStacking exp = new AutomatedStacking(classifier, data);
        AutomatedStackingFrame frame
                = new AutomatedStackingFrame(title, exp, this, maximumFractionDigits);
        frame.setVisible(true);
    }

}
