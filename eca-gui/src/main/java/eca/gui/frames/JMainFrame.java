/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.Reference;
import eca.client.EcaServiceClient;
import eca.client.EcaServiceClientImpl;
import eca.client.dto.EcaResponse;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.TechnicalStatusVisitor;
import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.converters.model.ClassificationModel;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.evaluation.EvaluationService;
import eca.data.db.JdbcQueryExecutor;
import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import eca.data.file.resource.FileResource;
import eca.data.file.resource.UrlResource;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AutomatedKNearestNeighbours;
import eca.dataminer.AutomatedNeuralNetwork;
import eca.dataminer.AutomatedRandomForests;
import eca.dataminer.AutomatedStacking;
import eca.dataminer.ExperimentUtil;
import eca.dictionary.ClassifiersNamesDictionary;
import eca.dictionary.EnsemblesNamesDictionary;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.CVIterativeBuilder;
import eca.ensemble.ConcurrentClassifier;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.Iterable;
import eca.ensemble.IterativeBuilder;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.RandomNetworks;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.gui.ConsoleTextArea;
import eca.gui.PanelBorderUtils;
import eca.gui.actions.CallbackAction;
import eca.gui.actions.DataBaseConnectionAction;
import eca.gui.actions.DataGeneratorCallback;
import eca.gui.actions.ExperimentRequestSender;
import eca.gui.actions.InstancesLoader;
import eca.gui.actions.ModelLoader;
import eca.gui.actions.UrlLoader;
import eca.gui.choosers.OpenDataFileChooser;
import eca.gui.choosers.OpenModelChooser;
import eca.gui.choosers.SaveDataFileChooser;
import eca.gui.dialogs.ClassifierOptionsDialogBase;
import eca.gui.dialogs.ClassifierBuilderDialog;
import eca.gui.dialogs.DataGeneratorDialog;
import eca.gui.dialogs.DatabaseConnectionDialog;
import eca.gui.dialogs.DecisionTreeOptionsDialog;
import eca.gui.dialogs.EcaServiceOptionsDialog;
import eca.gui.dialogs.EnsembleOptionsDialog;
import eca.gui.dialogs.EvaluationMethodOptionsDialog;
import eca.gui.dialogs.ExecutorDialog;
import eca.gui.dialogs.ExperimentRequestDialog;
import eca.gui.dialogs.J48OptionsDialog;
import eca.gui.dialogs.KNNOptionDialog;
import eca.gui.dialogs.LoadDialog;
import eca.gui.dialogs.LogisticOptionsDialogBase;
import eca.gui.dialogs.NetworkOptionsDialog;
import eca.gui.dialogs.RandomForestsOptionDialog;
import eca.gui.dialogs.RandomNetworkOptionsDialog;
import eca.gui.dialogs.SpinnerDialog;
import eca.gui.dialogs.StackingOptionsDialog;
import eca.gui.dictionary.ClassificationModelDictionary;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.logging.LoggerUtils;
import eca.gui.service.ExecutorService;
import eca.gui.tables.AttributesTable;
import eca.gui.tables.InstancesTable;
import eca.gui.tables.StatisticsTableBuilder;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import eca.trees.ID3;
import eca.trees.J48;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Implements the main application frame.
 *
 * @author Roman Batygin
 */
@Slf4j
public class JMainFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

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
    private static final String DATA_MINER_RANDOM_FORESTS_MENU_TEXT = "Автоматическое построение: Случайные леса";
    private static final String INDIVIDUAL_CLASSIFIERS_MENU_TEXT = "Индувидуальные алгоритмы";
    private static final String ENSEMBLE_CLASSIFIERS_MENU_TEXT = "Ансамблевые алгоритмы";
    private static final String DECISION_TREES_MENU_TEXT = "Деревья решений";
    private static final String CLASSIFIERS_HISTORY_MENU_TEXT = "История классификаторов";
    private static final String ATTRIBUTES_STATISTICS_MENU_TEXT = "Статистика по атрибутам";
    private static final String DEFAULT_URL_FOR_DATA_LOADING = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";
    private static final String EXCEED_DATA_LIST_SIZE_ERROR_FORMAT = "Число листов с данными не должно превышать %d!";
    private static final String CONSOLE_MENU_TEXT = "Открыть консоль";
    private static final String KNN_OPTIMIZER_MENU_TEXT =
            "Автоматическое построение: алгоритм k - взвешенных ближайших соседей";
    private static final String EXPERIMENT_REQUEST_MENU_TEXT = "Создать заявку на эксперимент";
    private static final String BUILD_TRAINING_DATA_LOADING_MESSAGE = "Пожалуйста подождите, идет подготовка данных...";

    private static final String EXPERIMENT_SUCCESS_MESSAGE_FORMAT =
            "Ваша заявка на эксперимент '%s' была успешно создана, пожалуйста ожидайте ответное письмо на email.";
    private static final String EXPERIMENT_TIMEOUT_MESSAGE = "There was a timeout.";
    private static final String EXPERIMENT_REQUEST_LOADING_MESSAGE =
            "Пожалуйста подождите, идет создание заявки на эксперимент...";

    private static final double WIDTH_COEFFICIENT = 0.8;
    private static final double HEIGHT_COEFFICIENT = 0.9;

    private final JDesktopPane dataPanels = new JDesktopPane();

    private JMenu windowsMenu;

    private ResultsHistory resultsHistory = new ResultsHistory();

    private int maximumFractionDigits;

    private boolean isStarted;

    private final EvaluationMethodOptionsDialog evaluationMethodOptionsDialog =
            new EvaluationMethodOptionsDialog(this);

    private ClassificationResultHistoryFrame resultHistoryFrame;

    private List<AbstractButton> disabledMenuElementList = new ArrayList<>();

    private final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());

    public JMainFrame() {
        Locale.setDefault(Locale.ENGLISH);
        this.init();
        this.createGUI();
        this.resultHistoryFrame = new ClassificationResultHistoryFrame(this, resultsHistory);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setEnabledMenuComponents(false);
        this.createWindowListener();
        this.setLocationRelativeTo(null);
    }

    private void init() {
        try {
            this.setTitle(CONFIG_SERVICE.getApplicationConfig().getProjectInfo().getTitle());
            if (CONFIG_SERVICE.getApplicationConfig().getFractionDigits() != null) {
                this.maximumFractionDigits = CONFIG_SERVICE.getApplicationConfig().getFractionDigits();
            } else {
                this.maximumFractionDigits = CommonDictionary.MAXIMUM_FRACTION_DIGITS;
            }
            this.setIconImage(ImageIO.read(CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON)));
            if (CONFIG_SERVICE.getApplicationConfig().getTooltipDismissTime() != null) {
                ToolTipManager.sharedInstance().setDismissDelay(
                        CONFIG_SERVICE.getApplicationConfig().getTooltipDismissTime());
            }
        } catch (Exception e) {
            LoggerUtils.error(log, e);
        }
    }

    private void closeWindow() {
        if (isStarted) {
            int exitResult = JOptionPane.showConfirmDialog(JMainFrame.this, ON_EXIT_TEXT, null,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (exitResult == JOptionPane.YES_OPTION) {
                JMainFrame.this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                System.exit(0);
            }
        } else {
            JMainFrame.this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
     * Implements data internal frame. This class provides the main
     * operations with training data set.
     */
    private class DataInternalFrame extends JInternalFrame {

        static final String UPPER_TITLE = "Информация о данных";
        static final String DATA_TITLE = "Таблица с данными";
        static final String ATTR_TITLE = "Выбранные атрибуты";
        static final String CLASS_TITLE = "Выбранный класс";
        static final String DATA_CHANGE_NAME_MENU_TEXT = "Изменение названия данных";
        static final String CHOOSE_COLOR_MENU_TEXT = "Выбор цвета фона";
        static final String DATA_NAME_TEXT = "Название:";
        static final String NEW_DATA_NAME_TEXT = "Новое название данных";
        static final String NUMBER_OF_INSTANCES_TEXT = "Число объектов: ";
        static final String NUMBER_OF_ATTRIBUTES_TEXT = "Число атрибутов: ";
        static final String CHOOSE_ALL_ATTRIBUTES_BUTTON_TEXT = "Выбрать все";
        static final String RESET_ALL_ATTRIBUTES_BUTTON_TEXT = "Сброс";
        static final int RELATION_NAME_FIELD_LENGTH = 30;
        static final int NUM_INSTANCES_FIELD_LENGTH = 6;
        static final int NUM_ATTRIBUTES_FIELD_LENGTH = 4;
        static final int ATTRS_PANEL_HEIGHT = 400;
        static final int CLASS_BOX_HEIGHT = 50;
        static final double ATTRS_PANEL_WIDTH_COEFFICIENT = 0.35;

        JPanel upperPanel;
        JPanel lowerPanel;
        JTextField relationNameTextField;
        JTextField numInstancesTextField;
        JTextField numAttributesTextField;

        JScrollPane dataScrollPane;
        JScrollPane attrScrollPane;
        JPanel attrPanel;

        JComboBox<String> classBox;
        InstancesTable instanceTable;
        AttributesTable attributesTable;
        JButton selectButton;
        JButton resetButton;

        JMenuItem menu;

        DataInternalFrame(Instances data, JMenuItem menu, int digits) throws Exception {
            this.setLayout(new GridBagLayout());
            this.createUpperPanel();
            this.createLowerPanel();
            this.setFrameColor(FRAME_COLOR);
            this.setMenu(menu);
            this.createPopMenu();
            this.setRelationInfo(data);
            this.convertDataToTables(data, digits);
            this.setClosable(true);
            this.setResizable(true);
            this.setMaximizable(true);
            this.pack();
        }

        void setMenu(JMenuItem menu) {
            this.menu = menu;
        }

        JMenuItem getMenu() {
            return menu;
        }

        Instances getData() throws Exception {
            return attributesTable.createData(relationNameTextField.getText());
        }

        void validateData() throws Exception {
            attributesTable.validateData();
        }

        void setFrameColor(Color color) {
            this.setBackground(color);
            upperPanel.setBackground(color);
            lowerPanel.setBackground(color);
            attrPanel.setBackground(color);
            dataScrollPane.setBackground(color);
            attrScrollPane.setBackground(color);
        }

        void createPopMenu() {
            JPopupMenu popMenu = new JPopupMenu();
            JMenuItem nameMenu = new JMenuItem(DATA_CHANGE_NAME_MENU_TEXT);
            JMenuItem colorMenu = new JMenuItem(CHOOSE_COLOR_MENU_TEXT);
            nameMenu.addActionListener(e -> {
                String newRelationName = (String) JOptionPane.showInputDialog(DataInternalFrame.this,
                        DATA_NAME_TEXT, NEW_DATA_NAME_TEXT, JOptionPane.INFORMATION_MESSAGE, null,
                        null, relationNameTextField.getText());
                if (newRelationName != null) {
                    String trimName = newRelationName.trim();
                    if (!StringUtils.isEmpty(trimName)) {
                        relationNameTextField.setText(trimName);
                        menu.setText(trimName);
                    }
                }
            });

            colorMenu.addActionListener(e -> {
                Color newBackgroundColor = JColorChooser.showDialog(DataInternalFrame.this,
                        colorMenu.getText(), getBackground());
                if (newBackgroundColor != null) {
                    setFrameColor(newBackgroundColor);
                }
            });

            popMenu.add(nameMenu);
            popMenu.add(colorMenu);
            this.setComponentPopupMenu(popMenu);
        }

        void setRelationInfo(Instances data) {
            relationNameTextField.setText(data.relationName());
            numInstancesTextField.setText(String.valueOf(data.numInstances()));
            numAttributesTextField.setText(String.valueOf(data.numAttributes()));
        }

        void createUpperPanel() {
            upperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            upperPanel.setBorder(PanelBorderUtils.createTitledBorder(UPPER_TITLE));
            relationNameTextField = new JTextField(RELATION_NAME_FIELD_LENGTH);
            relationNameTextField.setEditable(false);
            relationNameTextField.setBackground(Color.WHITE);
            numInstancesTextField = new JTextField(NUM_INSTANCES_FIELD_LENGTH);
            numInstancesTextField.setEditable(false);
            numInstancesTextField.setBackground(Color.WHITE);
            numAttributesTextField = new JTextField(NUM_ATTRIBUTES_FIELD_LENGTH);
            numAttributesTextField.setEditable(false);
            numAttributesTextField.setBackground(Color.WHITE);
            upperPanel.add(new JLabel(DATA_NAME_TEXT));
            upperPanel.add(relationNameTextField);
            upperPanel.add(new JLabel(NUMBER_OF_INSTANCES_TEXT));
            upperPanel.add(numInstancesTextField);
            upperPanel.add(new JLabel(NUMBER_OF_ATTRIBUTES_TEXT));
            upperPanel.add(numAttributesTextField);
            this.add(upperPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(5, 0, 5, 0), 0, 0));
        }

        void createLowerPanel() {
            lowerPanel = new JPanel(new GridBagLayout());
            dataScrollPane = new JScrollPane();
            dataScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setBorder(PanelBorderUtils.createTitledBorder(DATA_TITLE));
            this.createAttrPanel();
            lowerPanel.add(dataScrollPane, new GridBagConstraints(0, 0, 1, 2, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 2, 5), 0, 0));
            lowerPanel.add(attrPanel, new GridBagConstraints(1, 0, 1, 1, 0, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 2, 0), 0, 0));
            this.add(lowerPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }

        void createAttrPanel() {
            attrPanel = new JPanel(new GridBagLayout());
            attrPanel.setBorder(PanelBorderUtils.createTitledBorder(ATTR_TITLE));
            selectButton = new JButton(CHOOSE_ALL_ATTRIBUTES_BUTTON_TEXT);
            resetButton = new JButton(RESET_ALL_ATTRIBUTES_BUTTON_TEXT);

            selectButton.addActionListener(e -> attributesTable.selectAllAttributes());
            resetButton.addActionListener(e -> attributesTable.resetValues());

            attrScrollPane = new JScrollPane();
            attrScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            attrScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            double width = ATTRS_PANEL_WIDTH_COEFFICIENT * WIDTH_COEFFICIENT *
                    Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            attrScrollPane.setPreferredSize(new Dimension((int) width, ATTRS_PANEL_HEIGHT));
            classBox = new JComboBox<>();
            classBox.setBorder(PanelBorderUtils.createTitledBorder(CLASS_TITLE));
            Dimension classBoxDim = new Dimension((int) width, CLASS_BOX_HEIGHT);
            classBox.setPreferredSize(classBoxDim);
            classBox.setMinimumSize(classBoxDim);
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

        void convertDataToTables(Instances data, int digits) {
            for (int i = 0; i < data.numAttributes(); i++) {
                classBox.addItem(data.attribute(i).name());
            }
            data.setClassIndex(data.numAttributes() - 1);
            classBox.setSelectedIndex(data.classIndex());
            instanceTable = new InstancesTable(data, numInstancesTextField, digits);
            dataScrollPane.setViewportView(instanceTable);
            attributesTable = new AttributesTable(instanceTable, classBox);
            attrScrollPane.setViewportView(attributesTable);
            dataScrollPane.setComponentPopupMenu(instanceTable.getComponentPopupMenu());
        }

    } //End of class DataInternalFrame

    /**
     * Classifier model builder.
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
            evaluation =
                    EvaluationService.evaluateModel(model, data, evaluationMethodOptionsDialog.getEvaluationMethod(),
                            evaluationMethodOptionsDialog.numFolds(), evaluationMethodOptionsDialog.numTests(),
                            new Random());
        }

        Evaluation evaluation() {
            return evaluation;
        }

    } //End of class ModelBuilder

    /**
     * Training data builder callback.
     */
    private class DataBuilder implements CallbackAction {

        Instances data;

        @Override
        public void apply() throws Exception {
            data = selectedPanel().getData();
        }

        Instances getData() {
            return data;
        }
    }

    /**
     * Eca - service callback action.
     */
    private class EcaServiceAction implements CallbackAction {

        EcaServiceClient restClient;
        AbstractClassifier classifier;
        Instances data;
        EvaluationResults classifierDescriptor;

        EcaServiceAction(EcaServiceClient restClient, AbstractClassifier classifier, Instances data) {
            this.restClient = restClient;
            this.classifier = classifier;
            this.data = data;
        }

        @Override
        public void apply() throws Exception {
            classifierDescriptor = restClient.performRequest(classifier, data);
        }

        EvaluationResults getClassifierDescriptor() {
            return classifierDescriptor;
        }
    }

    /**
     * Classification results history model.
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
            addElement(String.format(HISTORY_FORMAT, simpleDateFormat.format(resultsFrameBase.getCreationDate()),
                    resultsFrameBase.classifier().getClass().getSimpleName()));
        }

        public ClassificationResultsFrameBase getFrame(int i) {
            return resultsFrameBases.get(i);
        }

    }

    private void executeWithEcaService(final ClassifierOptionsDialogBase frame) throws Exception {

        EcaServiceClientImpl restClient = new EcaServiceClientImpl();
        restClient.setEvaluationMethod(evaluationMethodOptionsDialog.getEvaluationMethod());
        restClient.setEvaluationUrl(CONFIG_SERVICE.getEcaServiceConfig().getEvaluationUrl());

        if (EvaluationMethod.CROSS_VALIDATION.equals(restClient.getEvaluationMethod())) {
            restClient.setNumFolds(evaluationMethodOptionsDialog.numFolds());
            restClient.setNumTests(evaluationMethodOptionsDialog.numTests());
        }

        EcaServiceAction ecaServiceAction = new EcaServiceAction(restClient, (AbstractClassifier) frame.classifier(),
                frame.data());

        LoadDialog progress = new LoadDialog(JMainFrame.this,
                ecaServiceAction, MODEL_BUILDING_MESSAGE);

        process(progress, () -> {
            EvaluationResults classifierDescriptor = ecaServiceAction.getClassifierDescriptor();
            resultsHistory.createResultFrame(frame.getTitle(), classifierDescriptor.getClassifier(),
                    frame.data(), classifierDescriptor.getEvaluation(), maximumFractionDigits);
        });
    }

    private void process(ExecutorDialog executorDialog, CallbackAction successAction) throws Exception {
        ExecutorService.process(executorDialog, successAction,
                () -> JOptionPane.showMessageDialog(JMainFrame.this,
                        executorDialog.getErrorMessageText(), null,
                        JOptionPane.WARNING_MESSAGE));
    }

    private void executeSimpleBuilding(ClassifierOptionsDialogBase frame) throws Exception {
        frame.showDialog();
        if (frame.dialogResult()) {
            List<String> options = Arrays.asList(((AbstractClassifier) frame.classifier()).getOptions());
            log.info("Starting evaluation for classifier {} with options: {} on data '{}'",
                    frame.classifier().getClass().getSimpleName(), options, frame.data().relationName());
            if (CONFIG_SERVICE.getEcaServiceConfig().getEnabled()) {
                executeWithEcaService(frame);
            } else {
                processSimpleBuilding(frame);
            }
        }
        frame.dispose();
    }

    private void processSimpleBuilding(ClassifierOptionsDialogBase frame) throws Exception {
        ModelBuilder builder = new ModelBuilder(frame.classifier(), frame.data());
        LoadDialog progress = new LoadDialog(JMainFrame.this,
                builder, MODEL_BUILDING_MESSAGE);

        process(progress, () -> {
            builder.evaluation().setTotalTimeMillis(progress.getTotalTimeMillis());
            resultsHistory.createResultFrame(frame.getTitle(), frame.classifier(), frame.data(),
                    builder.evaluation(), maximumFractionDigits);
        });
    }

    private void createTrainingData(DataBuilder dataBuilder, CallbackAction callbackAction) throws Exception {
        LoadDialog progress = new LoadDialog(JMainFrame.this, dataBuilder,
                BUILD_TRAINING_DATA_LOADING_MESSAGE);
        process(progress, callbackAction);
    }

    private DataInternalFrame selectedPanel() {
        return (DataInternalFrame) dataPanels.getSelectedFrame();
    }

    private void createGUI() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (WIDTH_COEFFICIENT * dim.getWidth()), (int) (HEIGHT_COEFFICIENT * dim.getHeight()));
        this.createMenu();
        dataPanels.setBackground(Color.GRAY);
        this.add(dataPanels);
    }

    private void createDataFrame(Instances data, int digits) throws Exception {
        if (dataPanels.getComponentCount() >= CONFIG_SERVICE.getApplicationConfig().getMaxDataListSize()) {
            throw new Exception(String.format(EXCEED_DATA_LIST_SIZE_ERROR_FORMAT,
                    CONFIG_SERVICE.getApplicationConfig().getMaxDataListSize()));
        }
        final DataInternalFrame dataInternalFrame =
                new DataInternalFrame(data, new JCheckBoxMenuItem(data.relationName()), digits);

        dataInternalFrame.addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                windowsMenu.remove(dataInternalFrame.getMenu());
                if (dataPanels.getComponentCount() == 0) {
                    setEnabledMenuComponents(false);
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
        dataInternalFrame.getMenu().addActionListener(event -> {
            try {
                dataInternalFrame.setSelected(true);
                dataInternalFrame.getMenu().setSelected(true);
            } catch (Exception e) {
                LoggerUtils.error(log, e);
            }
        });
        //--------------------------------------------
        dataPanels.add(dataInternalFrame);
        dataInternalFrame.setVisible(true);
        setEnabledMenuComponents(true);
        windowsMenu.add(dataInternalFrame.getMenu());
        isStarted = true;
    }

    public void createDataFrame(Instances data) throws Exception {
        createDataFrame(data, CommonDictionary.MAXIMUM_FRACTION_DIGITS);
    }

    private void setEnabledMenuComponents(boolean enabled) {
        for (AbstractButton abstractButton : disabledMenuElementList) {
            abstractButton.setEnabled(enabled);
        }
    }

    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        JMenu algorithmsMenu = new JMenu(CLASSIFIERS_MENU_TEXT);
        disabledMenuElementList.add(algorithmsMenu);
        JMenu dataMinerMenu = new JMenu(DATA_MINER_MENU_TEXT);
        disabledMenuElementList.add(dataMinerMenu);
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
        openFileMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.OPEN_ICON)));
        openFileMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        //-------------------------------
        openFileMenu.addActionListener(new ActionListener() {

            OpenDataFileChooser fileChooser;
            FileDataLoader dataLoader = new FileDataLoader();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new OpenDataFileChooser();
                    }
                    File file = fileChooser.openFile(JMainFrame.this);
                    if (file != null) {
                        dataLoader.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                        dataLoader.setSource(new FileResource(file));
                        InstancesLoader loader = new InstancesLoader(dataLoader);
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader, DATA_LOADING_MESSAGE);
                        process(progress, () -> createDataFrame(loader.data()));
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
        sampleMenu.addActionListener(e -> evaluationMethodOptionsDialog.showDialog());
        optionsMenu.add(sampleMenu);
        //------------------------------------------------
        JMenuItem digitsMenu = new JMenuItem(NUMBER_FORMAT_MENU_TEXT);
        digitsMenu.addActionListener(e -> {
            SpinnerDialog dialog = new SpinnerDialog(JMainFrame.this,
                    NUMBER_FORMAT_TITLE, DECIMAL_PLACES_TITLE, maximumFractionDigits,
                    CONFIG_SERVICE.getApplicationConfig().getMinFractionDigits(),
                    CONFIG_SERVICE.getApplicationConfig().getMaxFractionDigits());
            dialog.setVisible(true);
            if (dialog.dialogResult()) {
                maximumFractionDigits = dialog.getValue();
            }
            dialog.dispose();
        });
        optionsMenu.add(digitsMenu);
        //-------------------------------
        JMenuItem ecaServiceOptionsMenu = new JMenuItem(ECA_SERVICE_MENU_TEXT);
        ecaServiceOptionsMenu.addActionListener(e -> {
            EcaServiceOptionsDialog ecaServiceOptionsDialog = new EcaServiceOptionsDialog(JMainFrame.this);
            ecaServiceOptionsDialog.setVisible(true);
        });
        optionsMenu.add(ecaServiceOptionsMenu);

        fileMenu.add(openFileMenu);
        JMenuItem saveFileMenu = new JMenuItem(SAVE_FILE_MENU_TEXT);
        saveFileMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SAVE_ICON)));
        disabledMenuElementList.add(saveFileMenu);
        saveFileMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        fileMenu.add(saveFileMenu);
        //-------------------------------------------------
        saveFileMenu.addActionListener(new ActionListener() {

            SaveDataFileChooser fileChooser;
            FileDataSaver dataSaver = new FileDataSaver();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (dataValidated()) {
                        final DataBuilder dataBuilder = new DataBuilder();
                        createTrainingData(dataBuilder, () -> {
                            if (fileChooser == null) {
                                fileChooser = new SaveDataFileChooser();
                            }
                            fileChooser.setSelectedFile(new File(dataBuilder.getData().relationName()));
                            File file = fileChooser.getSelectedFile(JMainFrame.this);
                            if (file != null) {
                                dataSaver.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                                dataSaver.saveData(file, dataBuilder.getData());
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
        //---------------------------------------------
        JMenuItem dbMenu = new JMenuItem(DB_CONNECTION_MENU_TEXT);
        dbMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift D"));
        dbMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DATABASE_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(dbMenu);
        dbMenu.addActionListener(event -> {
            DatabaseConnectionDialog conn = new DatabaseConnectionDialog(JMainFrame.this);
            conn.setVisible(true);
            if (conn.dialogResult()) {
                try {
                    JdbcQueryExecutor connection = new JdbcQueryExecutor();
                    connection.setConnectionDescriptor(conn.getConnectionDescriptor());
                    connection.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                    LoadDialog progress = new LoadDialog(JMainFrame.this,
                            new DataBaseConnectionAction(connection),
                            DB_CONNECTION_WAITING_MESSAGE);

                    process(progress, () -> {
                        QueryFrame queryFrame = new QueryFrame(JMainFrame.this, connection);
                        queryFrame.setVisible(true);
                    });

                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
            conn.dispose();
        });
        //---------------------------------------------
        JMenuItem urlMenu = new JMenuItem(LOAD_DATA_FROM_NET_MENU_TEXT);
        urlMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift N"));
        urlMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.NET_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(urlMenu);
        urlMenu.addActionListener(event -> {
            String dataUrl = (String) JOptionPane.showInputDialog(JMainFrame.this,
                    URL_FILE_TEXT, LOAD_DATA_FROM_NET_TITLE, JOptionPane.INFORMATION_MESSAGE, null,
                    null, DEFAULT_URL_FOR_DATA_LOADING);

            if (dataUrl != null) {
                try {
                    FileDataLoader dataLoader = new FileDataLoader();
                    dataLoader.setSource(new UrlResource(new URL(dataUrl.trim())));
                    dataLoader.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                    UrlLoader loader = new UrlLoader(dataLoader);
                    LoadDialog progress = new LoadDialog(JMainFrame.this,
                            loader, DATA_LOADING_MESSAGE);
                    process(progress, () -> createDataFrame(loader.data()));
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //------------------------------------------------------
        JMenuItem loadModelMenu = new JMenuItem(LOAD_MODEL_MENU_TEXT);
        loadModelMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl M"));
        loadModelMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.LOAD_ICON)));
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
                                loader, MODEL_BUILDING_MESSAGE);

                        process(progress, () -> {
                            ClassificationModel model = loader.model();
                            String description;
                            int digits;
                            Map<String, String> properties = model.getAdditionalProperties();
                            if (model.getAdditionalProperties() != null) {
                                String descriptionProp =
                                        properties.get(ClassificationModelDictionary.DESCRIPTION_KEY);
                                description = descriptionProp != null ? descriptionProp
                                        : model.getClassifier().getClass().getSimpleName();
                                String digitsProp = properties.get(ClassificationModelDictionary.DIGITS_KEY);
                                digits = digitsProp != null ? Integer.valueOf(digitsProp) : maximumFractionDigits;
                            } else {
                                description = model.getClassifier().getClass().getSimpleName();
                                digits = maximumFractionDigits;
                            }

                            resultsHistory.createResultFrame(description,
                                    model.getClassifier(),
                                    model.getData(),
                                    model.getEvaluation(), digits);
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
        generatorMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.GENERATOR_ICON)));
        generatorMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl shift G"));
        fileMenu.addSeparator();
        fileMenu.add(generatorMenu);
        generatorMenu.addActionListener(event -> {
            DataGeneratorDialog dialog = new DataGeneratorDialog(JMainFrame.this);
            dialog.setVisible(true);
            if (dialog.dialogResult()) {
                try {
                    DataGeneratorCallback loader = new DataGeneratorCallback(dialog.getDataGenerator());
                    LoadDialog progress = new LoadDialog(JMainFrame.this, loader,
                            DATA_GENERATION_LOADING_MESSAGE);
                    process(progress, () -> createDataFrame(loader.getResult(), maximumFractionDigits));
                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    JOptionPane.showMessageDialog(JMainFrame.this, ex.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //---------------------------------------------
        JMenuItem exitMenu = new JMenuItem(EXIT_MENU_TEXT);
        exitMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.EXIT_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);
        //-------------------------------------------------
        exitMenu.addActionListener(e -> JMainFrame.this.closeWindow());
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
                } catch (Exception e) {
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
        JMenuItem knnOptimizerMenu = new JMenuItem(KNN_OPTIMIZER_MENU_TEXT);
        JMenuItem automatedRandomForestsMenu = new JMenuItem(DATA_MINER_RANDOM_FORESTS_MENU_TEXT);
        //--------------------------------------------------
        aNeuralMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        NeuralNetwork neuralNetwork = new NeuralNetwork(dataBuilder.getData());
                        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        AutomatedNeuralNetwork net =
                                new AutomatedNeuralNetwork(dataBuilder.getData(), neuralNetwork);
                        ExperimentFrame experimentFrame =
                                new AutomatedNeuralNetworkFrame(net, JMainFrame.this, maximumFractionDigits);
                        experimentFrame.setVisible(true);
                    });

                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //--------------------------------------------------
        modifiedHeteroEnsMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder,
                            () -> createEnsembleExperiment(new ModifiedHeterogeneousClassifier(),
                                    modifiedHeteroEnsMenu.getText(), dataBuilder.getData()));
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //--------------------------------------------------
        aHeteroEnsMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder,
                            () -> createEnsembleExperiment(new HeterogeneousClassifier(), aHeteroEnsMenu.getText(),
                                    dataBuilder.getData()));
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //--------------------------------------------------
        aAdaBoostMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder,
                            () -> createEnsembleExperiment(new AdaBoostClassifier(), aAdaBoostMenu.getText(),
                                    dataBuilder.getData()));
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //--------------------------------------------------
        aStackingMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder,
                            () -> createStackingExperiment(new StackingClassifier(), aStackingMenu.getText(),
                                    dataBuilder.getData()));
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        knnOptimizerMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
                        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                                new AutomatedKNearestNeighbours(dataBuilder.getData(), kNearestNeighbours);

                        AutomatedKNearestNeighboursFrame automatedKNearestNeighboursFrame =
                                new AutomatedKNearestNeighboursFrame(automatedKNearestNeighbours,
                                        JMainFrame.this, maximumFractionDigits);
                        automatedKNearestNeighboursFrame.setVisible(true);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //--------------------------------------------------
        automatedRandomForestsMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        AutomatedRandomForestsFrame automatedRandomForestsFrame =
                                new AutomatedRandomForestsFrame(automatedRandomForestsMenu.getText(),
                                        new AutomatedRandomForests(dataBuilder.getData()), JMainFrame.this,
                                        maximumFractionDigits);
                        automatedRandomForestsFrame.setVisible(true);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //----------------------------------------
        dataMinerMenu.add(aNeuralMenu);
        dataMinerMenu.add(aHeteroEnsMenu);
        dataMinerMenu.add(modifiedHeteroEnsMenu);
        dataMinerMenu.add(aAdaBoostMenu);
        dataMinerMenu.add(aStackingMenu);
        dataMinerMenu.add(knnOptimizerMenu);
        dataMinerMenu.add(automatedRandomForestsMenu);
        //-------------------------------
        JMenu classifiersMenu = new JMenu(INDIVIDUAL_CLASSIFIERS_MENU_TEXT);
        JMenu ensembleMenu = new JMenu(ENSEMBLE_CLASSIFIERS_MENU_TEXT);
        algorithmsMenu.add(classifiersMenu);
        algorithmsMenu.add(ensembleMenu);
        //------------------------------
        JMenu treesMenu = new JMenu(DECISION_TREES_MENU_TEXT);
        treesMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.TREE_ICON)));
        classifiersMenu.add(treesMenu);
        JMenuItem id3Item = new JMenuItem(ClassifiersNamesDictionary.ID3);
        JMenuItem c45Item = new JMenuItem(ClassifiersNamesDictionary.C45);
        JMenuItem cartItem = new JMenuItem(ClassifiersNamesDictionary.CART);
        JMenuItem chaidItem = new JMenuItem(ClassifiersNamesDictionary.CHAID);
        JMenuItem j48Item = new JMenuItem(ClassifiersNamesDictionary.J48);
        id3Item.addActionListener(e -> {
            if (dataValidated()) {
                createTreeOptionDialog(ClassifiersNamesDictionary.ID3, new ID3());
            }
        });
        c45Item.addActionListener(e -> {
            if (dataValidated()) {
                createTreeOptionDialog(ClassifiersNamesDictionary.C45, new C45());
            }
        });
        cartItem.addActionListener(e -> {
            if (dataValidated()) {
                createTreeOptionDialog(ClassifiersNamesDictionary.CART, new CART());
            }
        });
        chaidItem.addActionListener(e -> {
            if (dataValidated()) {
                createTreeOptionDialog(ClassifiersNamesDictionary.CHAID, new CHAID());
            }
        });
        j48Item.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        J48OptionsDialog frame = new J48OptionsDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.J48, new J48(), dataBuilder.getData());
                        executeSimpleBuilding(frame);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        treesMenu.add(id3Item);
        treesMenu.add(c45Item);
        treesMenu.add(cartItem);
        treesMenu.add(chaidItem);
        treesMenu.add(j48Item);
        //------------------------------------------------------------------
        JMenuItem logisticItem = new JMenuItem(ClassifiersNamesDictionary.LOGISTIC);
        logisticItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.LOGISTIC_ICON)));
        classifiersMenu.add(logisticItem);
        logisticItem.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        LogisticOptionsDialogBase frame = new LogisticOptionsDialogBase(JMainFrame.this,
                                ClassifiersNamesDictionary.LOGISTIC, new Logistic(), dataBuilder.getData());
                        executeSimpleBuilding(frame);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //------------------------------------------------------------------
        JMenuItem mlpItem = new JMenuItem(ClassifiersNamesDictionary.NEURAL_NETWORK);
        mlpItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.NEURAL_ICON)));
        classifiersMenu.add(mlpItem);
        mlpItem.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        NeuralNetwork neuralNetwork = new NeuralNetwork(dataBuilder.getData());
                        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        NetworkOptionsDialog frame = new NetworkOptionsDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.NEURAL_NETWORK, neuralNetwork,
                                dataBuilder.getData());
                        executeIterativeBuilding(frame, NETWORK_BUILDING_PROGRESS_TITLE);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //------------------------------------------------------------------
        JMenuItem knnItem = new JMenuItem(ClassifiersNamesDictionary.KNN);
        knnItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.KNN_ICON)));
        classifiersMenu.add(knnItem);
        knnItem.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
                        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        KNNOptionDialog frame = new KNNOptionDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.KNN, kNearestNeighbours, dataBuilder.getData());
                        executeSimpleBuilding(frame);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(),
                            null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //----------------------------------
        JMenuItem heterogeneousItem = new JMenuItem(EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE);
        heterogeneousItem.addActionListener(e -> {
            if (dataValidated()) {
                createEnsembleOptionDialog(EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE,
                        new HeterogeneousClassifier(), true);
            }
        });
        ensembleMenu.add(heterogeneousItem);
        //----------------------------------
        JMenuItem boostingItem = new JMenuItem(EnsemblesNamesDictionary.BOOSTING);
        boostingItem.addActionListener(e -> {
            if (dataValidated()) {
                createEnsembleOptionDialog(EnsemblesNamesDictionary.BOOSTING,
                        new AdaBoostClassifier(), false);
            }
        });
        //----------------------------------
        JMenuItem rndSubSpaceItem = new JMenuItem(EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE);
        rndSubSpaceItem.addActionListener(e -> {
            if (dataValidated()) {
                createEnsembleOptionDialog(EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE,
                        new ModifiedHeterogeneousClassifier(), true);
            }
        });
        ensembleMenu.add(rndSubSpaceItem);
        ensembleMenu.add(boostingItem);
        //----------------------------------
        JMenuItem rndForestsItem = new JMenuItem(EnsemblesNamesDictionary.RANDOM_FORESTS);
        rndForestsItem.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        RandomForestsOptionDialog frame =
                                new RandomForestsOptionDialog(JMainFrame.this,
                                        EnsemblesNamesDictionary.RANDOM_FORESTS,
                                        new RandomForests(dataBuilder.getData()), dataBuilder.getData());
                        executeIterativeBuilding(frame, ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        ensembleMenu.add(rndForestsItem);

        JMenuItem extraTreesItem = new JMenuItem(EnsemblesNamesDictionary.EXTRA_TREES);
        extraTreesItem.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        RandomForestsOptionDialog frame = new RandomForestsOptionDialog(JMainFrame.this,
                                EnsemblesNamesDictionary.EXTRA_TREES,
                                new ExtraTreesClassifier(dataBuilder.getData()),
                                dataBuilder.getData());
                        executeIterativeBuilding(frame, ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        ensembleMenu.add(extraTreesItem);

        //-------------------------------------------------
        JMenuItem stackingItem = new JMenuItem(EnsemblesNamesDictionary.STACKING);
        stackingItem.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        StackingOptionsDialog frame = new StackingOptionsDialog(JMainFrame.this,
                                EnsemblesNamesDictionary.STACKING, new StackingClassifier(),
                                dataBuilder.getData(),
                                maximumFractionDigits);
                        executeSimpleBuilding(frame);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        ensembleMenu.add(stackingItem);

        //----------------------------------
        JMenuItem rndNetworksItem = new JMenuItem(EnsemblesNamesDictionary.RANDOM_NETWORKS);
        rndNetworksItem.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        RandomNetworks randomNetworks = new RandomNetworks();
                        randomNetworks.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        RandomNetworkOptionsDialog networkOptionsDialog =
                                new RandomNetworkOptionsDialog(JMainFrame.this,
                                        EnsemblesNamesDictionary.RANDOM_NETWORKS, randomNetworks,
                                        dataBuilder.getData());
                        executeIterativeBuilding(networkOptionsDialog,
                                ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    });
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        ensembleMenu.add(rndNetworksItem);

        //-------------------------------------------------------------------
        JMenuItem historyMenu = new JMenuItem(CLASSIFIERS_HISTORY_MENU_TEXT);
        historyMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.HISTORY_ICON)));
        historyMenu.addActionListener(e -> resultHistoryFrame.setVisible(true));

        JMenuItem experimentRequestMenu = new JMenuItem(EXPERIMENT_REQUEST_MENU_TEXT);
        experimentRequestMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.EXPERIMENT_ICON)));
        disabledMenuElementList.add(experimentRequestMenu);
        experimentRequestMenu.addActionListener(new ActionListener() {

            EcaServiceClientImpl ecaServiceClient;
            ExperimentRequestDto experimentRequestDto;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (dataValidated()) {
                    try {
                        if (ecaServiceClient == null) {
                            ecaServiceClient = new EcaServiceClientImpl();
                        }
                        ecaServiceClient.setExperimentUrl(CONFIG_SERVICE.getEcaServiceConfig().getExperimentUrl());
                        final DataBuilder dataBuilder = new DataBuilder();
                        createTrainingData(dataBuilder, () -> {
                            ExperimentRequestDialog experimentRequestDialog =
                                    new ExperimentRequestDialog(JMainFrame.this);
                            experimentRequestDialog.showDialog(experimentRequestDto);
                            if (experimentRequestDialog.isDialogResult()) {
                                experimentRequestDto = experimentRequestDialog.createExperimentRequestDto();
                                experimentRequestDto.setData(dataBuilder.getData());
                                ExperimentRequestSender experimentRequestSender =
                                        new ExperimentRequestSender(ecaServiceClient, experimentRequestDto);
                                LoadDialog progress = new LoadDialog(JMainFrame.this,
                                        experimentRequestSender, EXPERIMENT_REQUEST_LOADING_MESSAGE);

                                process(progress, () -> {
                                    EcaResponse ecaResponse = experimentRequestSender.getEcaResponse();
                                    ecaResponse.getStatus().handle(new TechnicalStatusVisitor<Void>() {
                                        @Override
                                        public Void caseSuccessStatus() {
                                            JOptionPane.showMessageDialog(JMainFrame.this,
                                                    String.format(EXPERIMENT_SUCCESS_MESSAGE_FORMAT,
                                                            experimentRequestDto.getExperimentType()), null,
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            return null;
                                        }

                                        @Override
                                        public Void caseErrorStatus() {
                                            JOptionPane.showMessageDialog(JMainFrame.this,
                                                    ecaResponse.getErrorMessage(),
                                                    null, JOptionPane.ERROR_MESSAGE);
                                            return null;
                                        }

                                        @Override
                                        public Void caseTimeoutStatus() {
                                            JOptionPane.showMessageDialog(JMainFrame.this,
                                                    EXPERIMENT_TIMEOUT_MESSAGE, null,
                                                    JOptionPane.ERROR_MESSAGE);
                                            return null;
                                        }
                                    });
                                });
                            }
                        });
                    } catch (Exception e) {
                        LoggerUtils.error(log, e);
                        JOptionPane.showMessageDialog(JMainFrame.this,
                                e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        serviceMenu.add(historyMenu);
        serviceMenu.add(experimentRequestMenu);

        JMenuItem attrStatisticsMenu = new JMenuItem(ATTRIBUTES_STATISTICS_MENU_TEXT);
        attrStatisticsMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.STATISTICS_ICON)));
        disabledMenuElementList.add(attrStatisticsMenu);
        attrStatisticsMenu.addActionListener(event -> {
            if (dataValidated()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder();
                    createTrainingData(dataBuilder, () -> {
                        AttributesStatisticsFrame frame = new AttributesStatisticsFrame(dataBuilder.getData(),
                                JMainFrame.this, maximumFractionDigits);
                        frame.setVisible(true);
                    });
                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    JOptionPane.showMessageDialog(JMainFrame.this,
                            ex.getMessage(), null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        serviceMenu.add(attrStatisticsMenu);

        JMenuItem loggingMenu = new JMenuItem(CONSOLE_MENU_TEXT);
        loggingMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.CONSOLE_ICON)));
        loggingMenu.addActionListener(new ActionListener() {

            ConsoleFrame consoleFrame = new ConsoleFrame(JMainFrame.this,
                    ConsoleTextArea.getTextArea());

            @Override
            public void actionPerformed(ActionEvent evt) {
                consoleFrame.setVisible(true);
            }
        });
        serviceMenu.add(loggingMenu);

        this.setJMenuBar(menu);
    }

    private boolean isParallelClassifier(Classifier classifier) {
        if (classifier != null && classifier instanceof ConcurrentClassifier) {
            ConcurrentClassifier parallelClassifier = (ConcurrentClassifier) classifier;
            return parallelClassifier.getNumThreads() != null && parallelClassifier.getNumThreads() != 1;
        }
        return false;
    }

    /**
     * Executes iterative classifier building.
     *
     * @param frame           {@link ClassifierOptionsDialogBase} object
     * @param progressMessage progress message
     * @throws Exception
     */
    private void executeIterativeBuilding(final ClassifierOptionsDialogBase frame, String progressMessage) throws Exception {
        frame.showDialog();
        if (frame.dialogResult()) {
            List<String> options = Arrays.asList(((AbstractClassifier) frame.classifier()).getOptions());
            log.info("Starting evaluation for classifier {} with options: {} on data '{}'",
                    frame.classifier().getClass().getSimpleName(), options, frame.data().relationName());
            try {
                if (CONFIG_SERVICE.getEcaServiceConfig().getEnabled()) {
                    executeWithEcaService(frame);
                } else {
                    if (isParallelClassifier(frame.classifier())) {
                        processSimpleBuilding(frame);
                    } else {
                        processIterativeBuilding(frame, progressMessage);
                    }
                }
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(JMainFrame.this,
                        e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
            }

        }
    }

    private void processIterativeBuilding(ClassifierOptionsDialogBase frame, String progressMessage) throws Exception {
        IterativeBuilder iterativeBuilder = createIterativeClassifier((Iterable) frame.classifier(), frame.data());
        ClassifierBuilderDialog progress
                = new ClassifierBuilderDialog(JMainFrame.this, iterativeBuilder, progressMessage);
        process(progress, () -> resultsHistory.createResultFrame(frame.getTitle(), frame.classifier(),
                frame.data(), iterativeBuilder.evaluation(), maximumFractionDigits));
    }

    private boolean dataValidated() {
        try {
            selectedPanel().validateData();
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(),
                    null, JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void createTreeOptionDialog(final String title, final DecisionTreeClassifier tree) {
        try {
            final DataBuilder dataBuilder = new DataBuilder();
            createTrainingData(dataBuilder, () -> {
                DecisionTreeOptionsDialog frame
                        = new DecisionTreeOptionsDialog(JMainFrame.this, title, tree, dataBuilder.getData());
                executeSimpleBuilding(frame);
            });
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(),
                    null, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createEnsembleOptionDialog(final String title,
                                            final AbstractHeterogeneousClassifier heterogeneousClassifier,
                                            final boolean sample) {
        try {
            final DataBuilder dataBuilder = new DataBuilder();
            createTrainingData(dataBuilder, () -> {
                EnsembleOptionsDialog frame = new EnsembleOptionsDialog(JMainFrame.this,
                        title, heterogeneousClassifier, dataBuilder.getData(), maximumFractionDigits);
                frame.setSampleEnabled(sample);
                executeIterativeBuilding(frame, ENSEMBLE_BUILDING_PROGRESS_TITLE);
            });
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(JMainFrame.this,
                    e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
        }
    }

    private IterativeBuilder createIterativeClassifier(final Iterable model, final Instances data)
            throws Exception {
        switch (evaluationMethodOptionsDialog.getEvaluationMethod()) {
            case TRAINING_DATA:
                return model.getIterativeBuilder(data);
            case CROSS_VALIDATION:
                return new CVIterativeBuilder(model, data, evaluationMethodOptionsDialog.numFolds(),
                        evaluationMethodOptionsDialog.numTests());
            default:
                throw new IllegalArgumentException(String.format("Unexpected evaluation method: %s",
                        evaluationMethodOptionsDialog.getEvaluationMethod()));
        }
    }

    private void createEnsembleExperiment(AbstractHeterogeneousClassifier classifier,
                                          String title, Instances data) throws Exception {
        classifier.setClassifiersSet(ExperimentUtil.builtClassifiersSet(data, maximumFractionDigits));
        AutomatedHeterogeneousEnsemble exp = new AutomatedHeterogeneousEnsemble(classifier, data);
        AutomatedHeterogeneousEnsembleFrame frame
                = new AutomatedHeterogeneousEnsembleFrame(title, exp, this, maximumFractionDigits);
        frame.setVisible(true);
    }

    private void createStackingExperiment(StackingClassifier classifier,
                                          String title, Instances data) throws Exception {
        classifier.setClassifiers(ExperimentUtil.builtClassifiersSet(data, maximumFractionDigits));
        AutomatedStacking exp = new AutomatedStacking(classifier, data);
        AutomatedStackingFrame frame
                = new AutomatedStackingFrame(title, exp, this, maximumFractionDigits);
        frame.setVisible(true);
    }

}
