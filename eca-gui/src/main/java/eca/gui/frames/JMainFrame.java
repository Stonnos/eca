package eca.gui.frames;

import eca.client.dto.EcaResponse;
import eca.client.dto.EvaluationResponse;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.ExperimentResponse;
import eca.client.dto.TechnicalStatusVisitor;
import eca.client.instances.UploadInstancesCacheService;
import eca.client.listener.MessageListenerContainer;
import eca.client.listener.adapter.EvaluationListenerAdapter;
import eca.client.listener.adapter.ExperimentListenerAdapter;
import eca.client.messaging.MessageHandler;
import eca.client.rabbit.RabbitClient;
import eca.config.ConfigurationService;
import eca.config.EcaServiceConfig;
import eca.config.IconType;
import eca.config.RabbitConfiguration;
import eca.config.RabbitConnectionOptions;
import eca.config.registry.SingletonRegistry;
import eca.core.InstancesDataModel;
import eca.core.ModelSerializationHelper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationService;
import eca.core.model.ClassificationModel;
import eca.data.db.DatabaseSaver;
import eca.data.db.JdbcQueryExecutor;
import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import eca.data.file.resource.FileResource;
import eca.data.file.resource.UrlResource;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedDecisionTree;
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
import eca.ensemble.EnsembleUtils;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.Iterable;
import eca.ensemble.IterativeBuilder;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.RandomNetworks;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.gui.ConsoleTextArea;
import eca.gui.EvaluationResultsHistoryModel;
import eca.gui.PanelBorderUtils;
import eca.gui.actions.AbstractCallback;
import eca.gui.actions.CallbackAction;
import eca.gui.actions.ClassifierModelLoader;
import eca.gui.actions.ContingencyTableAction;
import eca.gui.actions.DataBaseConnectionAction;
import eca.gui.actions.DataGeneratorCallback;
import eca.gui.actions.DatabaseSaverAction;
import eca.gui.actions.ExperimentLoader;
import eca.gui.actions.InstancesLoader;
import eca.gui.actions.UrlLoader;
import eca.gui.choosers.OpenDataFileChooser;
import eca.gui.choosers.OpenModelChooser;
import eca.gui.choosers.SaveDataFileChooser;
import eca.gui.dialogs.ClassifierBuilderDialog;
import eca.gui.dialogs.ClassifierOptionsDialogBase;
import eca.gui.dialogs.ContingencyTableOptionsDialog;
import eca.gui.dialogs.DataGeneratorDialog;
import eca.gui.dialogs.DatabaseConnectionDialog;
import eca.gui.dialogs.DatabaseSaverDialog;
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
import eca.gui.dictionary.CommonDictionary;
import eca.gui.frames.results.ClassificationResultsFrameBase;
import eca.gui.frames.results.ClassificationResultsFrameFactory;
import eca.gui.listeners.ReferenceListener;
import eca.gui.logging.LoggerUtils;
import eca.gui.popup.PopupService;
import eca.gui.service.ExecutorService;
import eca.gui.tables.AttributesTable;
import eca.gui.tables.InstancesTable;
import eca.metrics.KNearestNeighbours;
import eca.model.EcaServiceRequestType;
import eca.model.EcaServiceTrack;
import eca.model.EcaServiceTrackStatus;
import eca.model.ReferenceWrapper;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.report.contingency.ContingencyTableReportModel;
import eca.statistics.contingency.ContingencyTable;
import eca.text.NumericFormatFactory;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import eca.trees.ID3;
import eca.trees.J48;
import eca.util.ClassifierNamesFactory;
import eca.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
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
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static eca.gui.GuiUtils.getScreenHeight;
import static eca.gui.GuiUtils.getScreenWidth;
import static eca.gui.GuiUtils.removeComponents;
import static eca.gui.GuiUtils.showFormattedErrorMessageDialog;
import static eca.gui.GuiUtils.showValidationErrorsDialog;
import static eca.gui.dictionary.KeyStrokes.DATA_GENERATOR_KEY_STROKE;
import static eca.gui.dictionary.KeyStrokes.LOAD_MODEL_KEY_STROKE;
import static eca.gui.dictionary.KeyStrokes.OPEN_DB_MENU_KEY_STROKE;
import static eca.gui.dictionary.KeyStrokes.OPEN_FILE_MENU_KEY_STROKE;
import static eca.gui.dictionary.KeyStrokes.REFERENCE_MENU_KEY_STROKE;
import static eca.gui.dictionary.KeyStrokes.SAVE_DB_MENU_KEY_STROKE;
import static eca.gui.dictionary.KeyStrokes.SAVE_FILE_MENU_KEY_STROKE;
import static eca.gui.dictionary.KeyStrokes.URL_MENU_KEY_STROKE;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_ADA_BOOST;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_DECISION_TREE;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_HETEROGENEOUS_ENSEMBLE;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_KNN;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_MODIFIED_HETEROGENEOUS_ENSEMBLE;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_NETWORKS;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_RANDOM_FORESTS;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_STACKING;
import static eca.util.ClassifierNamesFactory.getClassifierName;
import static eca.util.EcaServiceUtils.getFirstErrorAsString;
import static eca.util.UrlUtils.isValidUrl;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

/**
 * Implements the main application frame.
 *
 * @author Roman Batygin
 */
@Slf4j
public class JMainFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE = ConfigurationService.getApplicationConfigService();

    private static final Color FRAME_COLOR = new Color(198, 226, 255);

    private static final String ENSEMBLE_BUILDING_PROGRESS_TITLE = "Пожалуйста подождите, идет построение ансамбля...";
    private static final String NETWORK_BUILDING_PROGRESS_TITLE =
            "Пожалуйста подождите, идет обучение нейронной сети...";
    private static final String ON_EXIT_TEXT = "Вы уверены, что хотите выйти?";
    private static final String MODEL_BUILDING_MESSAGE = "Пожалуйста подождите, идет построение модели...";
    private static final String MODEL_LOADING_MESSAGE = "Пожалуйста подождите, идет загрузка модели...";
    private static final String EXPERIMENT_LOADING_MESSAGE = "Пожалуйста подождите, идет загрузка эксперимента...";
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
    private static final String ECA_SERVICE_OPTIONS_MENU_TEXT = "Настройки сервиса ECA";
    private static final String ECA_SERVICE_MENU_TEXT = "Сервис ECA";
    private static final String ECA_SERVICE_TRACKS_MENU_TEXT = "Запросы в Eca - сервис";
    private static final String SAVE_FILE_MENU_TEXT = "Сохранить...";
    private static final String DB_CONNECTION_MENU_TEXT = "Подключиться к базе данных";
    private static final String DB_CONNECTION_WAITING_MESSAGE =
            "Пожалуйста подождите, идет подключение к базе данных...";
    private static final String LOAD_MODEL_MENU_TEXT = "Загрузить модель";
    private static final String LOAD_EXPERIMENT_FROM_FILE_MENU_TEXT = "Загрузить эксперимент";
    private static final String LOAD_DATA_FROM_NET_MENU_TEXT = "Загрузить данные из сети";
    private static final String URL_FILE_TEXT = "URL файла:";
    private static final String EXPERIMENT_URL_FILE_TEXT = "URL файла:";
    private static final String LOAD_DATA_FROM_NET_TITLE = "Загрузка данных из сети";
    private static final String DATA_GENERATION_MENU_TEXT = "Генерация выборки";
    private static final String DATA_GENERATION_LOADING_MESSAGE = "Пожалуйста подождите, идет генерация данных...";
    private static final String EXIT_MENU_TEXT = "Выход";
    private static final String ABOUT_PROGRAM_MENU_TEXT = "О программе";
    private static final String SHOW_REFERENCE_MENU_TEXT = "Показать справку";
    private static final String INDIVIDUAL_CLASSIFIERS_MENU_TEXT = "Индувидуальные алгоритмы";
    private static final String ENSEMBLE_CLASSIFIERS_MENU_TEXT = "Ансамблевые алгоритмы";
    private static final String DECISION_TREES_MENU_TEXT = "Деревья решений";
    private static final String CLASSIFIERS_HISTORY_MENU_TEXT = "История классификаторов";
    private static final String ATTRIBUTES_STATISTICS_MENU_TEXT = "Статистика по атрибутам";
    private static final String DEFAULT_URL_FOR_DATA_LOADING = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";
    private static final String EXCEED_DATA_LIST_SIZE_ERROR_FORMAT = "Число листов с данными не должно превышать %d!";
    private static final String CONSOLE_MENU_TEXT = "Открыть консоль";
    private static final String EXPERIMENT_REQUEST_MENU_TEXT = "Создать заявку на эксперимент";
    private static final String BUILD_TRAINING_DATA_LOADING_MESSAGE = "Пожалуйста подождите, идет подготовка данных...";

    private static final String EXPERIMENT_SUCCESS_MESSAGE_FORMAT =
            "Ваша заявка на эксперимент '%s' была успешно создана.";

    private static final double WIDTH_COEFFICIENT = 0.8;
    private static final double HEIGHT_COEFFICIENT = 0.9;
    private static final String RANDOM_GENERATOR_MENU_TEXT = "Настройки генератора случайных чисел";
    private static final String RANDOM_GENERATOR_TITLE = "Настройки генератора";
    private static final String SEED_TEXT = "Начальное значение (seed):";
    private static final String OPTIMAL_CLASSIFIER_MENU_TEXT = "Подобрать оптимальный классификатор";
    private static final String SCATTER_DIAGRAM_MENU_TEXT = "Построение диаграмм рассеяния";
    private static final String DB_SAVE_MENU_TEXT = "Сохранить данные в базу данных";
    private static final String DB_SAVE_PROGRESS_MESSAGE_TEXT = "Пожалуйста подождите, идет сохранение данных...";
    private static final String SAVE_DATA_INFO_FORMAT = "Данные были успешно сохранены в таблицу '%s'";
    private static final String CONTINGENCY_TABLES_MENU_TEXT = "Таблицы сопряженности";
    private static final String STATISTICS_MENU_TEXT = "Статистика";
    private static final String CONTINGENCY_TABLE_LOADING_MESSAGE = "Пожалуйста подождите, идет построение таблицы...";
    private static final String EVALUATION_RESULTS_LOADER_MESSAGE =
            "Пожалуйста подождите, идет подготовка результатов классификации...";

    private static final String EVALUATION_RESULTS_QUEUE_FORMAT = "evaluation-results-%s";
    private static final String EXPERIMENT_QUEUE_FORMAT = "experiment-%s";
    private static final String EVALUATION_TIMEOUT_MESSAGE = "Произошел таймаут при построении модели";
    private static final String EVALUATION_CANCEL_MESSAGE = "Построение модели было прервано";
    private static final String EXPERIMENT_TIMEOUT_MESSAGE = "Произошел таймаут при построении эксперимента";
    private static final String EXPERIMENT_CANCEL_MESSAGE = "Построение эксперимента было прервано";
    private static final String ECA_SERVICE_DISABLED_MESSAGE =
            String.format("Данная опция не доступна. Задайте значение свойства %s в настройках сервиса ECA",
                    CommonDictionary.ECA_SERVICE_ENABLED);

    private static final String ECA_SERVICE_EVALUATION_REQUEST_SENT_MESSAGE
            = "Запрос на построение классификатора \"%s\" отправлен";

    private static final String ECA_SERVICE_OPTIMAL_CLASSIFIER_REQUEST_SENT_MESSAGE
            = "Запрос на построение оптимального классификатора отправлен";

    private static final String ECA_SERVICE_EXPERIMENT_REQUEST_SENT_MESSAGE
            = "Запрос на построение эксперимента \"%s\" отправлен";

    private static final String RECEIVED_EVALUATION_RESPONSE_FROM_ECA_SERVICE_MESSAGE
            = "Построение модели классификатора \"%s\" завершено";

    private static final String RECEIVED_OPTIMAL_CLASSIFIER_RESPONSE_FROM_ECA_SERVICE_MESSAGE
            = "Построение оптимального классификатора завершено";
    private static final String INVALID_FILE_URL_MESSAGE = "Задан некорректный url файла";
    private static final String DOWNLOAD_EXPERIMENT_TITLE = "Загрузка эксперимента";
    private static final String LOAD_EXPERIMENT_FORM_NET_TEXT = "Загрузить эксперимент из сети";
    private static final String EXPERIMENT_FINISHED_MESSAGE_TEXT_FORMAT =
            "Эксперимент '%s' успешно завершен. Загрузить результаты?";
    private static final String SUCCESS_RABBIT_CONNECTION_MESSAGE_FORMAT = "Соединение с %s:%d успешно установлено";
    private static final String RABBIT_CONNECTION_FAILED_MESSAGE_FORMAT =
            "Не удалось установить соединение с %s:%d";
    private static final String RABBIT_CONNECTION_MESSAGE_FORMAT = "Подключение к %s:%d...";
    private static final String RABBIT_CONNECTION_SHUTDOWN_MESSAGE_FORMAT = "Соединение с %s:%d разорвано";
    private static final String SAVE_DATA_TITLE = "Пожалуйста подождите, идет сохранение данных...";
    private static final String RESET_BUTTON_TOOLTIP_TEXT = "Установка настроек атрибутов и их типов по умолчанию";

    private final JDesktopPane dataPanels = new JDesktopPane();

    private JMenu windowsMenu;

    private int maximumFractionDigits;
    private int seed;

    private boolean started;

    private final EvaluationMethodOptionsDialog evaluationMethodOptionsDialog =
            new EvaluationMethodOptionsDialog(this);

    private ClassificationResultHistoryFrame resultHistoryFrame;

    private EcaServiceTrackFrame ecaServiceTrackFrame = new EcaServiceTrackFrame(this);

    private final PopupService popupService = new PopupService();

    private List<AbstractButton> disabledMenuElementList = newArrayList();

    private RabbitClient rabbitClient;
    private final UploadInstancesCacheService uploadInstancesCacheService = new UploadInstancesCacheService();

    private MessageListenerContainer messageListenerContainer;

    private String evaluationQueue;
    private String experimentQueue;

    private volatile boolean rabbitStarted;

    public JMainFrame() {
        Locale.setDefault(Locale.ENGLISH);
        this.init();
        this.createGUI();
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setEnabledMenuComponents(false);
        this.createWindowListener();
        this.setLocationRelativeTo(null);
    }

    public void initializeMessageListenerContainer() {
        try {
            generateQueueNames();
            updateMessageListenerContainerConfiguration(CONFIG_SERVICE.getEcaServiceConfig());
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
        }
    }

    public void initializeUploadInstancesClient() {
        try {
            updateUploadInstancesClientConfiguration(CONFIG_SERVICE.getEcaServiceConfig());
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
        }
    }

    private void init() {
        try {
            this.setTitle(CONFIG_SERVICE.getApplicationConfig().getProjectInfo().getTitle());
            this.maximumFractionDigits =
                    Utils.getIntValueOrDefault(CONFIG_SERVICE.getApplicationConfig().getFractionDigits(),
                            CommonDictionary.MAXIMUM_FRACTION_DIGITS);
            this.seed = Utils.getIntValueOrDefault(CONFIG_SERVICE.getApplicationConfig().getSeed(),
                    CommonDictionary.MIN_SEED);
            this.setIconImage(ImageIO.read(CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON)));
            this.resultHistoryFrame = new ClassificationResultHistoryFrame(this, new EvaluationResultsHistoryModel());
            initTooltipDismissDelay();
        } catch (Exception e) {
            LoggerUtils.error(log, e);
        }
    }

    private void initTooltipDismissDelay() {
        ToolTipManager.sharedInstance().setDismissDelay(
                Utils.getIntValueOrDefault(CONFIG_SERVICE.getApplicationConfig().getTooltipDismissTime(),
                        CommonDictionary.TOOLTIP_DISMISS));
    }

    private void closeWindow() {
        if (started) {
            int exitResult = JOptionPane.showConfirmDialog(JMainFrame.this, ON_EXIT_TEXT, null,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (exitResult == JOptionPane.YES_OPTION) {
                JMainFrame.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                System.exit(0);
            }
        } else {
            JMainFrame.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        static final String RESET_ALL_ATTRIBUTES_BUTTON_TEXT = "По умолчанию";
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

        DataInternalFrame(Instances data, JMenuItem menu, int digits) {
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
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.pack();
        }

        @Override
        public void dispose() {
            removeComponents(this);
            super.dispose();
        }

        void setMenu(JMenuItem menu) {
            this.menu = menu;
        }

        JMenuItem getMenu() {
            return menu;
        }

        InstancesDataModel getFilteredValidData() throws Exception {
            return instanceTable.createAndFilterValidData();
        }

        InstancesDataModel getSimpleData() throws Exception {
            return instanceTable.createSimpleData();
        }

        void validateData(boolean validateAttributes) {
            instanceTable.validateData(validateAttributes);
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
            nameMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.EDIT_ICON)));
            JMenuItem colorMenu = new JMenuItem(CHOOSE_COLOR_MENU_TEXT);
            colorMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COLOR_ICON)));
            nameMenu.addActionListener(e -> {
                String newRelationName = (String) JOptionPane.showInputDialog(DataInternalFrame.this,
                        DATA_NAME_TEXT, NEW_DATA_NAME_TEXT, JOptionPane.INFORMATION_MESSAGE, null,
                        null, relationNameTextField.getText());
                if (newRelationName != null) {
                    String trimName = newRelationName.trim();
                    if (!StringUtils.isEmpty(trimName)) {
                        relationNameTextField.setText(trimName);
                        menu.setText(trimName);
                        instanceTable.setRelationName(trimName);
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
            dataScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
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
            resetButton.setToolTipText(RESET_BUTTON_TOOLTIP_TEXT);

            selectButton.addActionListener(e -> attributesTable.selectAllAttributes());
            resetButton.addActionListener(e -> attributesTable.resetValues());

            attrScrollPane = new JScrollPane();
            attrScrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
            attrScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
            double width = ATTRS_PANEL_WIDTH_COEFFICIENT * WIDTH_COEFFICIENT * getScreenWidth();
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
            classBox.setSelectedIndex(data.classIndex());
            instanceTable = new InstancesTable(data, numInstancesTextField, classBox, digits);
            dataScrollPane.setViewportView(instanceTable);
            attributesTable = new AttributesTable(instanceTable, classBox);
            instanceTable.setAttributesTable(attributesTable);
            attrScrollPane.setViewportView(attributesTable);
            dataScrollPane.setComponentPopupMenu(instanceTable.getComponentPopupMenu());
        }

        void clear() {
            instanceTable.clear();
        }
    } //End of class DataInternalFrame

    /**
     * Classifier model builder.
     */
    private class ModelBuilder extends AbstractCallback<Evaluation> {

        Classifier model;
        Instances data;

        ModelBuilder(Classifier model, Instances data) {
            this.model = model;
            this.data = data;
        }

        @Override
        protected Evaluation performAndGetResult() throws Exception {
            return EvaluationService.evaluateModel(model, data, evaluationMethodOptionsDialog.getEvaluationMethod(),
                    evaluationMethodOptionsDialog.numFolds(), evaluationMethodOptionsDialog.numTests(), seed);
        }
    } //End of class ModelBuilder

    /**
     * Training data builder callback.
     */
    private class DataBuilder extends AbstractCallback<InstancesDataModel> {

        /**
         * Validates and filter instances using {@link eca.filter.ConstantAttributesFilter}?
         */
        boolean validateAndFilter = true;

        DataBuilder() {
        }

        DataBuilder(boolean validateAndFilter) {
            this.validateAndFilter = validateAndFilter;
        }

        @Override
        protected InstancesDataModel performAndGetResult() throws Exception {
            return validateAndFilter ? selectedPanel().getFilteredValidData() : selectedPanel().getSimpleData();
        }
    }

    private ClassificationResultsFrameBase createEvaluationResults(String title,
                                                                   ReferenceWrapper<Classifier> classifier,
                                                                   Instances data,
                                                                   Evaluation evaluation,
                                                                   int digits) throws Exception {
        ClassificationResultsFrameBase classificationResultsFrameBase =
                ClassificationResultsFrameFactory.buildClassificationResultsFrameBase(JMainFrame.this, title,
                        classifier, data, evaluation, digits);
        resultHistoryFrame.addItem(classificationResultsFrameBase);
        return classificationResultsFrameBase;
    }

    private void createEvaluationResultsAsync(String title,
                                              ReferenceWrapper<Classifier> classifier,
                                              Instances data,
                                              Evaluation evaluation,
                                              int digits) throws Exception {
        AbstractCallback<ClassificationResultsFrameBase> callback =
                new AbstractCallback<ClassificationResultsFrameBase>() {
                    @Override
                    protected ClassificationResultsFrameBase performAndGetResult() throws Exception {
                        return createEvaluationResults(title, classifier, data, evaluation, digits);
                    }
                };
        LoadDialog progress = new LoadDialog(JMainFrame.this, callback, EVALUATION_RESULTS_LOADER_MESSAGE);
        processAsyncTask(progress, () -> callback.getResult().setVisible(true));
    }

    private void executeWithEcaService(final ClassifierOptionsDialogBase frame, InstancesDataModel instancesDataModel) {
        rabbitClient.setEvaluationMethod(evaluationMethodOptionsDialog.getEvaluationMethod());
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethodOptionsDialog.getEvaluationMethod())) {
            rabbitClient.setNumFolds(evaluationMethodOptionsDialog.numFolds());
            rabbitClient.setNumTests(evaluationMethodOptionsDialog.numTests());
            rabbitClient.setSeed(seed);
        }
        String correlationId = UUID.randomUUID().toString();
        AbstractClassifier classifier = (AbstractClassifier) frame.classifier();
        EcaServiceTrack ecaServiceTrack = EcaServiceTrack.builder()
                .correlationId(correlationId)
                .requestType(EcaServiceRequestType.CLASSIFIER)
                .status(EcaServiceTrackStatus.READY)
                .details(frame.getTitle())
                .relationName(frame.data().relationName())
                .additionalData(Utils.getClassifierInputOptionsMap(classifier))
                .build();
        addEcaServiceTrack(ecaServiceTrack);
        try {
            String dataUuid = uploadInstancesCacheService.uploadInstances(instancesDataModel);
            rabbitClient.sendEvaluationRequest(classifier, dataUuid, evaluationQueue, correlationId);
            updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.REQUEST_SENT);
            String infoMessage =
                    String.format(ECA_SERVICE_EVALUATION_REQUEST_SENT_MESSAGE, ecaServiceTrack.getDetails());
            popupService.showInfoPopup(infoMessage, this);
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
            updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.ERROR);
            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
        }
    }

    private void performTaskWithDataAndAttributesValidation(CallbackAction action) {
        if (isDataAndAttributesValid()) {
            try {
                action.apply();
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
                showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
            }
        }
    }

    private void processAsyncTask(ExecutorDialog executorDialog, CallbackAction successAction) throws Exception {
        ExecutorService.process(executorDialog, successAction,
                () -> showFormattedErrorMessageDialog(JMainFrame.this, executorDialog.getErrorMessageText()));
    }

    private void executeSimpleBuilding(ClassifierOptionsDialogBase frame, InstancesDataModel instancesDataModel)
            throws Exception {
        frame.showDialog();
        if (frame.dialogResult()) {
            List<String> options = Arrays.asList(((AbstractClassifier) frame.classifier()).getOptions());
            log.info("Starting evaluation for classifier {} with options: {} on data '{}'",
                    frame.classifier().getClass().getSimpleName(), options, frame.data().relationName());
            if (Boolean.TRUE.equals(CONFIG_SERVICE.getEcaServiceConfig().getEnabled())) {
                executeWithEcaService(frame, instancesDataModel);
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

        processAsyncTask(progress, () -> {
            builder.getResult().setTotalTimeMillis(progress.getTotalTimeMillis());
            createEvaluationResultsAsync(frame.getTitle(), frame.classifierReference(), frame.data(),
                    builder.getResult(), maximumFractionDigits);
        });
    }

    private void prepareTrainingData(DataBuilder dataBuilder, CallbackAction callbackAction) throws Exception {
        LoadDialog progress = new LoadDialog(JMainFrame.this, dataBuilder,
                BUILD_TRAINING_DATA_LOADING_MESSAGE);
        processAsyncTask(progress, callbackAction);
    }

    private DataInternalFrame selectedPanel() {
        return (DataInternalFrame) dataPanels.getSelectedFrame();
    }

    private void createGUI() {
        this.setSize((int) (WIDTH_COEFFICIENT * getScreenWidth()), (int) (HEIGHT_COEFFICIENT * getScreenHeight()));
        this.populateMenuBar();
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

        dataInternalFrame.getMenu().addActionListener(event -> {
            try {
                dataInternalFrame.setSelected(true);
                dataInternalFrame.getMenu().setSelected(true);
            } catch (Exception e) {
                LoggerUtils.error(log, e);
            }
        });

        dataPanels.add(dataInternalFrame);
        dataInternalFrame.setVisible(true);
        setEnabledMenuComponents(true);
        windowsMenu.add(dataInternalFrame.getMenu());
        started = true;
    }

    public void createDataFrame(Instances data) throws Exception {
        createDataFrame(data, CommonDictionary.MAXIMUM_FRACTION_DIGITS);
    }

    private void setEnabledMenuComponents(boolean enabled) {
        for (AbstractButton abstractButton : disabledMenuElementList) {
            abstractButton.setEnabled(enabled);
        }
    }

    private void populateMenuBar() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        JMenu algorithmsMenu = new JMenu(CLASSIFIERS_MENU_TEXT);
        disabledMenuElementList.add(algorithmsMenu);
        JMenu dataMinerMenu = new JMenu(DATA_MINER_MENU_TEXT);
        disabledMenuElementList.add(dataMinerMenu);
        JMenu statisticsMenu = new JMenu(STATISTICS_MENU_TEXT);
        disabledMenuElementList.add(statisticsMenu);
        JMenu optionsMenu = new JMenu(OPTIONS_MENU_TEXT);
        JMenu serviceMenu = new JMenu(SERVICE_MENU_TEXT);
        windowsMenu = new JMenu(WINDOWS_MENU_TEXT);
        JMenu referenceMenu = new JMenu(REFERENCE_MENU_TEXT);
        fillFileMenu(fileMenu);
        fillAlgorithmsMenu(algorithmsMenu);
        fillDataMinerMenu(dataMinerMenu);
        fillStatisticsMenu(statisticsMenu);
        fillServiceMenu(serviceMenu);
        fillOptionsMenu(optionsMenu);
        fillReferenceMenu(referenceMenu);
        menu.add(fileMenu);
        menu.add(algorithmsMenu);
        menu.add(dataMinerMenu);
        menu.add(statisticsMenu);
        menu.add(optionsMenu);
        menu.add(serviceMenu);
        menu.add(windowsMenu);
        menu.add(referenceMenu);
        this.setJMenuBar(menu);
    }

    private void fillAlgorithmsMenu(JMenu algorithmsMenu) {
        JMenu classifiersMenu = new JMenu(INDIVIDUAL_CLASSIFIERS_MENU_TEXT);
        JMenu ensembleMenu = new JMenu(ENSEMBLE_CLASSIFIERS_MENU_TEXT);
        algorithmsMenu.add(classifiersMenu);
        algorithmsMenu.add(ensembleMenu);

        JMenu treesMenu = new JMenu(DECISION_TREES_MENU_TEXT);
        treesMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.TREE_ICON)));
        classifiersMenu.add(treesMenu);
        JMenuItem id3Item = new JMenuItem(ClassifiersNamesDictionary.ID3);
        JMenuItem c45Item = new JMenuItem(ClassifiersNamesDictionary.C45);
        JMenuItem cartItem = new JMenuItem(ClassifiersNamesDictionary.CART);
        JMenuItem chaidItem = new JMenuItem(ClassifiersNamesDictionary.CHAID);
        JMenuItem j48Item = new JMenuItem(ClassifiersNamesDictionary.J48);
        id3Item.addActionListener(event -> performTaskWithDataAndAttributesValidation(
                () -> createTreeOptionDialog(ClassifiersNamesDictionary.ID3, new ID3())));
        c45Item.addActionListener(event -> performTaskWithDataAndAttributesValidation(
                () -> createTreeOptionDialog(ClassifiersNamesDictionary.C45, new C45())));
        cartItem.addActionListener(event -> performTaskWithDataAndAttributesValidation(
                () -> createTreeOptionDialog(ClassifiersNamesDictionary.CART, new CART())));
        chaidItem.addActionListener(event -> performTaskWithDataAndAttributesValidation(
                () -> createTreeOptionDialog(ClassifiersNamesDictionary.CHAID, new CHAID())));
        j48Item.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        J48OptionsDialog frame = new J48OptionsDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.J48, new J48(), dataBuilder.getResult().getData());
                        executeSimpleBuilding(frame, dataBuilder.getResult());
                    });
                })
        );

        treesMenu.add(id3Item);
        treesMenu.add(c45Item);
        treesMenu.add(cartItem);
        treesMenu.add(chaidItem);
        treesMenu.add(j48Item);

        JMenuItem logisticItem = new JMenuItem(ClassifiersNamesDictionary.LOGISTIC);
        logisticItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.LOGISTIC_ICON)));
        classifiersMenu.add(logisticItem);
        logisticItem.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        LogisticOptionsDialogBase frame = new LogisticOptionsDialogBase(JMainFrame.this,
                                ClassifiersNamesDictionary.LOGISTIC, new Logistic(), dataBuilder.getResult().getData());
                        executeSimpleBuilding(frame, dataBuilder.getResult());
                    });
                })
        );

        JMenuItem mlpItem = new JMenuItem(ClassifiersNamesDictionary.NEURAL_NETWORK);
        mlpItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.NEURAL_ICON)));
        classifiersMenu.add(mlpItem);
        mlpItem.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        NeuralNetwork neuralNetwork = new NeuralNetwork(dataBuilder.getResult().getData());
                        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        neuralNetwork.setSeed(seed);
                        NetworkOptionsDialog frame = new NetworkOptionsDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.NEURAL_NETWORK, neuralNetwork,
                                dataBuilder.getResult().getData());
                        executeIterativeBuilding(frame, dataBuilder.getResult(), NETWORK_BUILDING_PROGRESS_TITLE);
                    });
                })
        );

        JMenuItem knnItem = new JMenuItem(ClassifiersNamesDictionary.KNN);
        knnItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.KNN_ICON)));
        classifiersMenu.add(knnItem);
        knnItem.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
                        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        KNNOptionDialog frame = new KNNOptionDialog(JMainFrame.this,
                                ClassifiersNamesDictionary.KNN, kNearestNeighbours, dataBuilder.getResult().getData());
                        executeSimpleBuilding(frame, dataBuilder.getResult());
                    });
                })
        );

        JMenuItem heterogeneousItem = new JMenuItem(EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE);
        heterogeneousItem.addActionListener(e ->
                performTaskWithDataAndAttributesValidation(
                        () -> createEnsembleOptionDialog(EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE,
                                new HeterogeneousClassifier(), true))
        );
        ensembleMenu.add(heterogeneousItem);

        JMenuItem boostingItem = new JMenuItem(EnsemblesNamesDictionary.BOOSTING);
        boostingItem.addActionListener(e ->
                performTaskWithDataAndAttributesValidation(
                        () -> createEnsembleOptionDialog(EnsemblesNamesDictionary.BOOSTING, new AdaBoostClassifier(),
                                false))
        );

        JMenuItem rndSubSpaceItem = new JMenuItem(EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE);
        rndSubSpaceItem.addActionListener(e ->
                performTaskWithDataAndAttributesValidation(
                        () -> createEnsembleOptionDialog(EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE,
                                new ModifiedHeterogeneousClassifier(), true))
        );
        ensembleMenu.add(rndSubSpaceItem);
        ensembleMenu.add(boostingItem);

        JMenuItem rndForestsItem = new JMenuItem(EnsemblesNamesDictionary.RANDOM_FORESTS);
        rndForestsItem.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        RandomForests randomForests = new RandomForests(dataBuilder.getResult().getData());
                        randomForests.setSeed(seed);
                        RandomForestsOptionDialog frame =
                                new RandomForestsOptionDialog(JMainFrame.this,
                                        EnsemblesNamesDictionary.RANDOM_FORESTS, randomForests,
                                        dataBuilder.getResult().getData());
                        executeIterativeBuilding(frame, dataBuilder.getResult(), ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    });
                })
        );
        ensembleMenu.add(rndForestsItem);

        JMenuItem extraTreesItem = new JMenuItem(EnsemblesNamesDictionary.EXTRA_TREES);
        extraTreesItem.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        ExtraTreesClassifier extraTreesClassifier =
                                new ExtraTreesClassifier(dataBuilder.getResult().getData());
                        extraTreesClassifier.setSeed(seed);
                        RandomForestsOptionDialog frame = new RandomForestsOptionDialog(JMainFrame.this,
                                EnsemblesNamesDictionary.EXTRA_TREES, extraTreesClassifier,
                                dataBuilder.getResult().getData());
                        executeIterativeBuilding(frame, dataBuilder.getResult(), ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    });
                })
        );
        ensembleMenu.add(extraTreesItem);

        JMenuItem stackingItem = new JMenuItem(EnsemblesNamesDictionary.STACKING);
        stackingItem.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        StackingClassifier stackingClassifier = new StackingClassifier();
                        stackingClassifier.setSeed(seed);
                        StackingOptionsDialog frame =
                                new StackingOptionsDialog(JMainFrame.this, EnsemblesNamesDictionary.STACKING,
                                        stackingClassifier, dataBuilder.getResult().getData(), maximumFractionDigits);
                        executeSimpleBuilding(frame, dataBuilder.getResult());
                    });
                })
        );
        ensembleMenu.add(stackingItem);

        JMenuItem rndNetworksItem = new JMenuItem(EnsemblesNamesDictionary.RANDOM_NETWORKS);
        rndNetworksItem.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        RandomNetworks randomNetworks = new RandomNetworks();
                        randomNetworks.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        randomNetworks.setSeed(seed);
                        RandomNetworkOptionsDialog networkOptionsDialog =
                                new RandomNetworkOptionsDialog(JMainFrame.this,
                                        EnsemblesNamesDictionary.RANDOM_NETWORKS, randomNetworks,
                                        dataBuilder.getResult().getData());
                        executeIterativeBuilding(networkOptionsDialog, dataBuilder.getResult(),
                                ENSEMBLE_BUILDING_PROGRESS_TITLE);
                    });
                })
        );
        ensembleMenu.add(rndNetworksItem);
    }

    private void fillDataMinerMenu(JMenu dataMinerMenu) {
        JMenuItem aNeuralMenu = new JMenuItem(DATA_MINER_NETWORKS);
        JMenuItem aHeteroEnsMenu = new JMenuItem(DATA_MINER_HETEROGENEOUS_ENSEMBLE);
        JMenuItem modifiedHeteroEnsMenu =
                new JMenuItem(DATA_MINER_MODIFIED_HETEROGENEOUS_ENSEMBLE);
        JMenuItem aAdaBoostMenu = new JMenuItem(DATA_MINER_ADA_BOOST);
        JMenuItem aStackingMenu = new JMenuItem(DATA_MINER_STACKING);
        JMenuItem knnOptimizerMenu = new JMenuItem(DATA_MINER_KNN);
        JMenuItem automatedRandomForestsMenu = new JMenuItem(DATA_MINER_RANDOM_FORESTS);
        JMenuItem automatedDecisionTreeMenu = new JMenuItem(DATA_MINER_DECISION_TREE);

        aNeuralMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        NeuralNetwork neuralNetwork = new NeuralNetwork(dataBuilder.getResult().getData());
                        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        AutomatedNeuralNetwork automatedNeuralNetwork =
                                new AutomatedNeuralNetwork(dataBuilder.getResult().getData(), neuralNetwork);
                        automatedNeuralNetwork.setSeed(seed);
                        ExperimentFrame experimentFrame =
                                new AutomatedNeuralNetworkFrame(automatedNeuralNetwork, JMainFrame.this,
                                        maximumFractionDigits);
                        experimentFrame.setVisible(true);
                    });
                })
        );

        modifiedHeteroEnsMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder,
                            () -> createEnsembleExperiment(new ModifiedHeterogeneousClassifier(),
                                    modifiedHeteroEnsMenu.getText(), dataBuilder.getResult().getData()));
                })
        );

        aHeteroEnsMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder,
                            () -> createEnsembleExperiment(new HeterogeneousClassifier(), aHeteroEnsMenu.getText(),
                                    dataBuilder.getResult().getData()));
                })
        );

        aAdaBoostMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder,
                            () -> createEnsembleExperiment(new AdaBoostClassifier(), aAdaBoostMenu.getText(),
                                    dataBuilder.getResult().getData()));
                })
        );

        aStackingMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder,
                            () -> createStackingExperiment(new StackingClassifier(),
                                    dataBuilder.getResult().getData()));
                })
        );

        knnOptimizerMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
                        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
                        AutomatedKNearestNeighbours automatedKNearestNeighbours =
                                new AutomatedKNearestNeighbours(dataBuilder.getResult().getData(), kNearestNeighbours);
                        automatedKNearestNeighbours.setSeed(seed);
                        AutomatedKNearestNeighboursFrame automatedKNearestNeighboursFrame =
                                new AutomatedKNearestNeighboursFrame(automatedKNearestNeighbours,
                                        JMainFrame.this, maximumFractionDigits);
                        automatedKNearestNeighboursFrame.setVisible(true);
                    });
                })
        );

        automatedRandomForestsMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        AutomatedRandomForests automatedRandomForests =
                                new AutomatedRandomForests(dataBuilder.getResult().getData());
                        automatedRandomForests.setSeed(seed);
                        AutomatedRandomForestsFrame automatedRandomForestsFrame =
                                new AutomatedRandomForestsFrame(automatedRandomForests, JMainFrame.this,
                                        maximumFractionDigits);
                        automatedRandomForestsFrame.setVisible(true);
                    });
                })
        );

        automatedDecisionTreeMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        AutomatedDecisionTree automatedDecisionTree =
                                new AutomatedDecisionTree(dataBuilder.getResult().getData());
                        automatedDecisionTree.setSeed(seed);
                        AutomatedDecisionTreeFrame automatedDecisionTreeFrame =
                                new AutomatedDecisionTreeFrame(automatedDecisionTree, JMainFrame.this,
                                        maximumFractionDigits);
                        automatedDecisionTreeFrame.setVisible(true);
                    });
                })
        );

        dataMinerMenu.add(aNeuralMenu);
        dataMinerMenu.add(aHeteroEnsMenu);
        dataMinerMenu.add(modifiedHeteroEnsMenu);
        dataMinerMenu.add(aAdaBoostMenu);
        dataMinerMenu.add(aStackingMenu);
        dataMinerMenu.add(knnOptimizerMenu);
        dataMinerMenu.add(automatedRandomForestsMenu);
        dataMinerMenu.add(automatedDecisionTreeMenu);
    }

    private void fillStatisticsMenu(JMenu statisticsMenu) {
        JMenuItem attrStatisticsMenu = new JMenuItem(ATTRIBUTES_STATISTICS_MENU_TEXT);
        attrStatisticsMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.STATISTICS_ICON)));
        attrStatisticsMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        AttributesStatisticsFrame frame =
                                new AttributesStatisticsFrame(dataBuilder.getResult().getData(), JMainFrame.this,
                                        maximumFractionDigits);
                        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        frame.setVisible(true);
                    });
                })
        );
        statisticsMenu.add(attrStatisticsMenu);

        JMenuItem scatterDiagramMenu = new JMenuItem(SCATTER_DIAGRAM_MENU_TEXT);
        scatterDiagramMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SCATTER_ICON)));
        scatterDiagramMenu.addActionListener(event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        ScatterDiagramsFrame scatterDiagramsFrame =
                                new ScatterDiagramsFrame(dataBuilder.getResult().getData(), JMainFrame.this);
                        scatterDiagramsFrame.setVisible(true);
                    });
                })
        );
        statisticsMenu.add(scatterDiagramMenu);

        JMenuItem contingencyTablesMenu = new JMenuItem(CONTINGENCY_TABLES_MENU_TEXT);
        contingencyTablesMenu.addActionListener(contingencyTableActionListener());
        statisticsMenu.add(contingencyTablesMenu);
    }

    private void fillServiceMenu(JMenu serviceMenu) {
        JMenuItem historyMenu = new JMenuItem(CLASSIFIERS_HISTORY_MENU_TEXT);
        historyMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.HISTORY_ICON)));
        historyMenu.addActionListener(e -> resultHistoryFrame.setVisible(true));

        JMenu ecaServiceMenu = new JMenu(ECA_SERVICE_MENU_TEXT);
        ecaServiceMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.ECA_SERVICE_ICON)));
        disabledMenuElementList.add(ecaServiceMenu);

        JMenuItem experimentRequestMenu = new JMenuItem(EXPERIMENT_REQUEST_MENU_TEXT);
        experimentRequestMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.EXPERIMENT_ICON)));
        experimentRequestMenu.addActionListener(experimentRequestActionListener());
        JMenuItem optimalClassifierMenu = new JMenuItem(OPTIMAL_CLASSIFIER_MENU_TEXT);
        optimalClassifierMenu.setIcon(
                new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.OPTIMAL_CLASSIFIER_ICON)));
        optimalClassifierMenu.addActionListener(optimalClassifierActionListener());

        JMenuItem ecaServiceTracksMenu = new JMenuItem(ECA_SERVICE_TRACKS_MENU_TEXT);
        ecaServiceTracksMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.ECA_SERVICE_TRACKS_ICON)));
        ecaServiceTracksMenu.addActionListener(event -> ecaServiceTrackFrame.setVisible(true));

        ecaServiceMenu.add(experimentRequestMenu);
        ecaServiceMenu.add(optimalClassifierMenu);
        ecaServiceMenu.add(ecaServiceTracksMenu);
        serviceMenu.add(historyMenu);
        serviceMenu.add(ecaServiceMenu);

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
    }

    private void fillReferenceMenu(JMenu referenceMenu) {
        JMenuItem aboutProgramMenu = new JMenuItem(ABOUT_PROGRAM_MENU_TEXT);
        JMenuItem reference = new JMenuItem(SHOW_REFERENCE_MENU_TEXT);
        reference.setAccelerator(KeyStroke.getKeyStroke(REFERENCE_MENU_KEY_STROKE));

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

        reference.addActionListener(new ReferenceListener(JMainFrame.this));
        referenceMenu.add(aboutProgramMenu);
        referenceMenu.add(reference);
    }

    private void fillOptionsMenu(JMenu optionsMenu) {
        JMenuItem sampleMenu = new JMenuItem(EVALUATION_METHOD_MENU_TEXT);
        sampleMenu.addActionListener(e -> evaluationMethodOptionsDialog.showDialog());
        optionsMenu.add(sampleMenu);

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

        JMenuItem seedMenu = new JMenuItem(RANDOM_GENERATOR_MENU_TEXT);
        seedMenu.addActionListener(e -> {
            SpinnerDialog dialog = new SpinnerDialog(JMainFrame.this, RANDOM_GENERATOR_TITLE, SEED_TEXT, seed,
                    CommonDictionary.MIN_SEED, CommonDictionary.MAX_SEED);
            dialog.setVisible(true);
            if (dialog.dialogResult()) {
                seed = dialog.getValue();
            }
            dialog.dispose();
        });
        optionsMenu.add(seedMenu);

        JMenuItem ecaServiceOptionsMenu = new JMenuItem(ECA_SERVICE_OPTIONS_MENU_TEXT);
        ecaServiceOptionsMenu.addActionListener(e -> {
            EcaServiceConfig ecaServiceConfig = CONFIG_SERVICE.getEcaServiceConfig();
            RabbitConnectionOptions rabbitConnectionOptions = ecaServiceConfig.getRabbitConnectionOptions();
            RabbitConnectionOptions prevRabbitConnectionOptions =
                    new RabbitConnectionOptions(rabbitConnectionOptions.getHost(), rabbitConnectionOptions.getPort(),
                            rabbitConnectionOptions.getUsername(), rabbitConnectionOptions.getPassword());
            EcaServiceConfig prevConfig =
                    new EcaServiceConfig(ecaServiceConfig.getEnabled(),
                            prevRabbitConnectionOptions,
                            ecaServiceConfig.getEvaluationRequestQueue(),
                            ecaServiceConfig.getEvaluationOptimizerRequestQueue(),
                            ecaServiceConfig.getExperimentRequestQueue(), ecaServiceConfig.getDataLoaderUrl(),
                            ecaServiceConfig.getTokenUrl(), ecaServiceConfig.getClientId(),
                            ecaServiceConfig.getClientSecret()
                    );
            EcaServiceOptionsDialog ecaServiceOptionsDialog = new EcaServiceOptionsDialog(JMainFrame.this);
            ecaServiceOptionsDialog.setVisible(true);
            if (ecaServiceOptionsDialog.isDialogResult()) {
                try {
                    CONFIG_SERVICE.saveEcaServiceConfig();
                    updateUploadInstancesClientConfiguration(CONFIG_SERVICE.getEcaServiceConfig());
                    updateMessageListenerContainerConfiguration(prevConfig);
                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    JOptionPane.showMessageDialog(JMainFrame.this, ex.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
            ecaServiceOptionsDialog.dispose();
        });
        optionsMenu.add(ecaServiceOptionsMenu);
    }

    private void fillFileMenu(JMenu fileMenu) {
        JMenuItem openFileMenu = new JMenuItem(OPEN_FILE_MENU_TEXT);
        openFileMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.OPEN_ICON)));
        openFileMenu.setAccelerator(KeyStroke.getKeyStroke(OPEN_FILE_MENU_KEY_STROKE));
        openFileMenu.addActionListener(openFileActionListener());
        fileMenu.add(openFileMenu);

        JMenuItem saveFileMenu = new JMenuItem(SAVE_FILE_MENU_TEXT);
        saveFileMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SAVE_ICON)));
        disabledMenuElementList.add(saveFileMenu);
        saveFileMenu.setAccelerator(KeyStroke.getKeyStroke(SAVE_FILE_MENU_KEY_STROKE));
        fileMenu.add(saveFileMenu);
        saveFileMenu.addActionListener(saveFileActionListener());

        JMenuItem dbMenu = new JMenuItem(DB_CONNECTION_MENU_TEXT);
        dbMenu.setAccelerator(KeyStroke.getKeyStroke(OPEN_DB_MENU_KEY_STROKE));
        dbMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DATABASE_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(dbMenu);
        dbMenu.addActionListener(dbConnectionActionListener());

        JMenuItem dbSaverMenu = new JMenuItem(DB_SAVE_MENU_TEXT);
        dbSaverMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DB_SAVE_ICON)));
        dbSaverMenu.setAccelerator(KeyStroke.getKeyStroke(SAVE_DB_MENU_KEY_STROKE));
        disabledMenuElementList.add(dbSaverMenu);
        dbSaverMenu.addActionListener(dbSaverActionListener());
        fileMenu.add(dbSaverMenu);

        JMenuItem urlMenu = new JMenuItem(LOAD_DATA_FROM_NET_MENU_TEXT);
        urlMenu.setAccelerator(KeyStroke.getKeyStroke(URL_MENU_KEY_STROKE));
        urlMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.NET_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(urlMenu);
        urlMenu.addActionListener(urlLoaderActionListener());

        JMenuItem loadModelMenu = new JMenuItem(LOAD_MODEL_MENU_TEXT);
        loadModelMenu.setAccelerator(KeyStroke.getKeyStroke(LOAD_MODEL_KEY_STROKE));
        loadModelMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.LOAD_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(loadModelMenu);
        loadModelMenu.addActionListener(loadModelActionListener());

        JMenuItem loadExperimentFromFileMenu = new JMenuItem(LOAD_EXPERIMENT_FROM_FILE_MENU_TEXT);
        loadExperimentFromFileMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.LOAD_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(loadExperimentFromFileMenu);
        loadExperimentFromFileMenu.addActionListener(loadExperimentFromFileActionListener());

        JMenuItem loadExperimentFromUrlMenu = new JMenuItem(LOAD_EXPERIMENT_FORM_NET_TEXT);
        loadExperimentFromUrlMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.NET_ICON)));
        fileMenu.add(loadExperimentFromUrlMenu);
        loadExperimentFromUrlMenu.addActionListener(loadExperimentFromUrlActionListener());

        JMenuItem generatorMenu = new JMenuItem(DATA_GENERATION_MENU_TEXT);
        generatorMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.GENERATOR_ICON)));
        generatorMenu.setAccelerator(KeyStroke.getKeyStroke(DATA_GENERATOR_KEY_STROKE));
        fileMenu.addSeparator();
        fileMenu.add(generatorMenu);
        generatorMenu.addActionListener(dataGeneratorActionListener());

        JMenuItem exitMenu = new JMenuItem(EXIT_MENU_TEXT);
        exitMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.EXIT_ICON)));
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);
        exitMenu.addActionListener(e -> JMainFrame.this.closeWindow());
    }

    /**
     * Executes iterative classifier building.
     *
     * @param frame              - classifier options dialog base object
     * @param instancesDataModel - instances data model
     * @param progressMessage    - progress message
     */
    private void executeIterativeBuilding(final ClassifierOptionsDialogBase frame,
                                          final InstancesDataModel instancesDataModel,
                                          final String progressMessage) {
        frame.showDialog();
        if (frame.dialogResult()) {
            List<String> options = Arrays.asList(((AbstractClassifier) frame.classifier()).getOptions());
            log.info("Starting evaluation for classifier {} with options: {} on data '{}'",
                    frame.classifier().getClass().getSimpleName(), options, frame.data().relationName());
            try {
                if (Boolean.TRUE.equals(CONFIG_SERVICE.getEcaServiceConfig().getEnabled())) {
                    executeWithEcaService(frame, instancesDataModel);
                } else {
                    if (EnsembleUtils.isConcurrentClassifier(frame.classifier())) {
                        processSimpleBuilding(frame);
                    } else {
                        processIterativeBuilding(frame, progressMessage);
                    }
                }
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
                showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
            }

        }
        frame.dispose();
    }

    private void processIterativeBuilding(ClassifierOptionsDialogBase frame, String progressMessage) throws Exception {
        IterativeBuilder iterativeBuilder = createIterativeClassifier((Iterable) frame.classifier(), frame.data());
        ClassifierBuilderDialog progress
                = new ClassifierBuilderDialog(JMainFrame.this, iterativeBuilder, progressMessage);
        processAsyncTask(progress,
                () -> createEvaluationResultsAsync(frame.getTitle(), frame.classifierReference(), frame.data(),
                        iterativeBuilder.evaluation(), maximumFractionDigits));
    }

    private boolean isDataAndAttributesValid() {
        return validateDataInternal(true);
    }

    private boolean isDataValid() {
        return validateDataInternal(false);
    }

    private boolean validateDataInternal(boolean validateAttributes) {
        try {
            selectedPanel().validateData(validateAttributes);
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
            return false;
        }
        return true;
    }

    private void createTreeOptionDialog(final String title, final DecisionTreeClassifier tree) {
        try {
            final DataBuilder dataBuilder = new DataBuilder();
            prepareTrainingData(dataBuilder, () -> {
                tree.setSeed(seed);
                DecisionTreeOptionsDialog frame =
                        new DecisionTreeOptionsDialog(JMainFrame.this, title, tree, dataBuilder.getResult().getData());
                executeSimpleBuilding(frame, dataBuilder.getResult());
            });
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
        }
    }

    private void createEnsembleOptionDialog(final String title,
                                            final AbstractHeterogeneousClassifier heterogeneousClassifier,
                                            final boolean sample) {
        try {
            final DataBuilder dataBuilder = new DataBuilder();
            prepareTrainingData(dataBuilder, () -> {
                heterogeneousClassifier.setSeed(seed);
                EnsembleOptionsDialog frame = new EnsembleOptionsDialog(JMainFrame.this,
                        title, heterogeneousClassifier, dataBuilder.getResult().getData(), maximumFractionDigits);
                frame.setSampleEnabled(sample);
                executeIterativeBuilding(frame, dataBuilder.getResult(), ENSEMBLE_BUILDING_PROGRESS_TITLE);
            });
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
        }
    }

    private IterativeBuilder createIterativeClassifier(final Iterable model, final Instances data)
            throws Exception {
        switch (evaluationMethodOptionsDialog.getEvaluationMethod()) {
            case TRAINING_DATA:
                return model.getIterativeBuilder(data);
            case CROSS_VALIDATION:
                return new CVIterativeBuilder(model, data, evaluationMethodOptionsDialog.numFolds(),
                        evaluationMethodOptionsDialog.numTests(), seed);
            default:
                throw new IllegalArgumentException(String.format("Unexpected evaluation method: %s",
                        evaluationMethodOptionsDialog.getEvaluationMethod()));
        }
    }

    private void createEnsembleExperiment(AbstractHeterogeneousClassifier classifier, String title, Instances data) {
        classifier.setClassifiersSet(ExperimentUtil.builtClassifiersSet(data, maximumFractionDigits));
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble =
                new AutomatedHeterogeneousEnsemble(classifier, data);
        automatedHeterogeneousEnsemble.setSeed(seed);
        AutomatedHeterogeneousEnsembleFrame frame =
                new AutomatedHeterogeneousEnsembleFrame(title, automatedHeterogeneousEnsemble, this,
                        maximumFractionDigits);
        frame.setVisible(true);
    }

    private void createStackingExperiment(StackingClassifier classifier, Instances data) {
        classifier.setClassifiers(ExperimentUtil.builtClassifiersSet(data, maximumFractionDigits));
        AutomatedStacking automatedStacking = new AutomatedStacking(classifier, data);
        automatedStacking.setSeed(seed);
        AutomatedStackingFrame frame = new AutomatedStackingFrame(automatedStacking, this, maximumFractionDigits);
        frame.setVisible(true);
    }

    private void configureRabbitClient() {
        EcaServiceConfig ecaServiceConfig = CONFIG_SERVICE.getEcaServiceConfig();
        rabbitClient = RabbitConfiguration.getRabbitConfiguration().configureRabbitClient(ecaServiceConfig);
    }

    private void configureAndStartMessageListenerContainer() {
        EcaServiceConfig ecaServiceConfig = CONFIG_SERVICE.getEcaServiceConfig();
        messageListenerContainer =
                RabbitConfiguration.getRabbitConfiguration().configureMessageListenerContainer(ecaServiceConfig);
        addConnectionSuccessCallback();
        addConnectionFailedCallback();
        addConnectionShutdownCallback();
        addEvaluationListenerAdapterIfAbsent();
        addExperimentListenerAdapterIfAbsent();
        messageListenerContainer.start();
    }

    private void addConnectionSuccessCallback() {
        messageListenerContainer.setSuccessCallback(connectionFactory -> {
            popupService.showInfoPopup(
                    String.format(SUCCESS_RABBIT_CONNECTION_MESSAGE_FORMAT, connectionFactory.getHost(),
                            connectionFactory.getPort()), this);
        });
    }

    private void addConnectionShutdownCallback() {
        messageListenerContainer.setShutdownCallback(connectionFactory -> {
            popupService.showInfoPopup(
                    String.format(RABBIT_CONNECTION_SHUTDOWN_MESSAGE_FORMAT, connectionFactory.getHost(),
                            connectionFactory.getPort()), this);
        });
    }

    private void addConnectionFailedCallback() {
        messageListenerContainer.setFailedCallback(connectionFactory -> {
            try {
                popupService.showInfoPopup(
                        String.format(RABBIT_CONNECTION_FAILED_MESSAGE_FORMAT, connectionFactory.getHost(),
                                connectionFactory.getPort()), this);
                resetRabbitConfiguration();
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
                JOptionPane.showMessageDialog(JMainFrame.this, ex.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateMessageListenerContainerConfiguration(EcaServiceConfig prevConfig) throws Exception {
        EcaServiceConfig currentConfig = CONFIG_SERVICE.getEcaServiceConfig();
        //Restart message listener container if needs
        if (Boolean.TRUE.equals(currentConfig.getEnabled())) {
            if (!rabbitStarted ||
                    !prevConfig.getRabbitConnectionOptions().equals(currentConfig.getRabbitConnectionOptions())) {
                popupService.showInfoPopup(String.format(RABBIT_CONNECTION_MESSAGE_FORMAT,
                        currentConfig.getRabbitConnectionOptions().getHost(),
                        currentConfig.getRabbitConnectionOptions().getPort()), this);
                resetRabbitConfiguration();
                configureAndStartMessageListenerContainer();
                configureRabbitClient();
                rabbitStarted = true;
            }
        } else {
            resetRabbitConfiguration();
        }
        updateQueues(currentConfig);
    }

    private void updateUploadInstancesClientConfiguration(EcaServiceConfig ecaServiceConfig) {
        uploadInstancesCacheService.getUploadInstancesClient().setDataLoaderUrl(ecaServiceConfig.getDataLoaderUrl());
        uploadInstancesCacheService.getUploadInstancesClient().getOauth2TokenProvider().setTokenUrl(
                ecaServiceConfig.getTokenUrl());
        uploadInstancesCacheService.getUploadInstancesClient().getOauth2TokenProvider().setClientId(
                ecaServiceConfig.getClientId());
        uploadInstancesCacheService.getUploadInstancesClient().getOauth2TokenProvider().setClientSecret(
                ecaServiceConfig.getClientSecret());
    }

    private void updateQueues(EcaServiceConfig ecaServiceConfig) {
        Optional.ofNullable(rabbitClient).ifPresent(client -> {
            client.setEvaluationRequestQueue(ecaServiceConfig.getEvaluationRequestQueue());
            client.setEvaluationOptimizerRequestQueue(ecaServiceConfig.getEvaluationOptimizerRequestQueue());
            client.setExperimentRequestQueue(ecaServiceConfig.getExperimentRequestQueue());
        });
    }

    private void resetRabbitConfiguration() throws Exception {
        if (rabbitStarted) {
            messageListenerContainer.stop();
            rabbitClient.getRabbitSender().getConnectionManager().close();
            rabbitStarted = false;
        }
    }

    private void generateQueueNames() {
        evaluationQueue = String.format(EVALUATION_RESULTS_QUEUE_FORMAT, UUID.randomUUID());
        experimentQueue = String.format(EXPERIMENT_QUEUE_FORMAT, UUID.randomUUID());
    }

    private void addEvaluationListenerAdapterIfAbsent() {
        if (!messageListenerContainer.getRabbitListenerAdapters().containsKey(evaluationQueue)) {
            MessageHandler<EvaluationResponse> evaluationResponseMessageHandler =
                    createEvaluationResultsMessageHandler();
            EvaluationListenerAdapter evaluationListenerAdapter =
                    new EvaluationListenerAdapter(RabbitConfiguration.getRabbitConfiguration().getMessageConverter(),
                            evaluationResponseMessageHandler);
            messageListenerContainer.getRabbitListenerAdapters().put(evaluationQueue, evaluationListenerAdapter);
        }
    }

    private void addExperimentListenerAdapterIfAbsent() {
        if (!messageListenerContainer.getRabbitListenerAdapters().containsKey(experimentQueue)) {
            MessageHandler<ExperimentResponse> experimentMessageHandler = createExperimentMessageHandler();
            ExperimentListenerAdapter experimentListenerAdapter =
                    new ExperimentListenerAdapter(RabbitConfiguration.getRabbitConfiguration().getMessageConverter(),
                            experimentMessageHandler);
            messageListenerContainer.getRabbitListenerAdapters().put(experimentQueue, experimentListenerAdapter);
        }
    }

    private void updateEcaServiceTrackStatus(String correlationId, EcaResponse ecaResponse) {
        ecaResponse.getStatus().handle(new TechnicalStatusVisitor() {
            @Override
            public void caseSuccessStatus() {
                updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.COMPLETED);
            }

            @Override
            public void caseInProgressStatus() {
                updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.IN_PROGRESS);
            }

            @Override
            public void caseErrorStatus() {
                updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.ERROR);
            }

            @Override
            public void caseTimeoutStatus() {
                updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.TIMEOUT);
            }

            @Override
            public void caseValidationErrorStatus() {
                updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.ERROR);
            }

            @Override
            public void caseCanceledStatus() {
                updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.CANCELED);
            }
        });
    }

    private MessageHandler<EvaluationResponse> createEvaluationResultsMessageHandler() {
        return (evaluationResponse, basicProperties) -> {
            try {
                log.info("Received evaluation response with correlation id [{}], status [{}], request id [{}]",
                        basicProperties.getCorrelationId(), evaluationResponse.getStatus(),
                        evaluationResponse.getRequestId());
                EcaServiceTrack ecaServiceTrack = getEcaServiceTrack(basicProperties.getCorrelationId());
                updateEcaServiceTrackStatus(ecaServiceTrack.getCorrelationId(), evaluationResponse);
                String infoMessage;
                if (ecaServiceTrack.getRequestType().equals(EcaServiceRequestType.OPTIMAL_CLASSIFIER)) {
                    infoMessage = RECEIVED_OPTIMAL_CLASSIFIER_RESPONSE_FROM_ECA_SERVICE_MESSAGE;
                } else {
                    infoMessage = String.format(RECEIVED_EVALUATION_RESPONSE_FROM_ECA_SERVICE_MESSAGE,
                            ecaServiceTrack.getDetails());
                }
                popupService.showInfoPopup(infoMessage, this);
                evaluationResponse.getStatus().handle(new TechnicalStatusVisitor() {
                    @Override
                    public void caseSuccessStatus() {
                        try {
                            ClassificationModel classificationModel = downloadModel(evaluationResponse);
                            String title =
                                    ClassifierNamesFactory.getClassifierName(classificationModel.getClassifier());
                            ClassificationResultsFrameBase classificationResultsFrameBase =
                                    createEvaluationResults(title,
                                            new ReferenceWrapper<>(classificationModel.getClassifier()),
                                            classificationModel.getData(),
                                            classificationModel.getEvaluation(),
                                            maximumFractionDigits);
                            classificationResultsFrameBase.setVisible(true);
                        } catch (Exception ex) {
                            LoggerUtils.error(log, ex);
                            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                        }
                    }

                    @Override
                    public void caseInProgressStatus() {
                        //Not implemented
                    }

                    @Override
                    public void caseErrorStatus() {
                        showFormattedErrorMessageDialog(JMainFrame.this, getFirstErrorAsString(evaluationResponse));
                    }

                    @Override
                    public void caseTimeoutStatus() {
                        JOptionPane.showMessageDialog(JMainFrame.this, EVALUATION_TIMEOUT_MESSAGE, null,
                                JOptionPane.ERROR_MESSAGE);
                    }

                    @Override
                    public void caseValidationErrorStatus() {
                        showValidationErrorsDialog(JMainFrame.this, evaluationResponse);
                    }

                    @Override
                    public void caseCanceledStatus() {
                        JOptionPane.showMessageDialog(JMainFrame.this, EVALUATION_CANCEL_MESSAGE, null,
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
                showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
            }
        };
    }

    private MessageHandler<ExperimentResponse> createExperimentMessageHandler() {
        return (experimentResponse, basicProperties) -> {
            try {
                log.info("Received experiment response with correlation id [{}], status [{}], request id [{}]",
                        basicProperties.getCorrelationId(), experimentResponse.getStatus(),
                        experimentResponse.getRequestId());
                EcaServiceTrack ecaServiceTrack = getEcaServiceTrack(basicProperties.getCorrelationId());
                updateEcaServiceTrackStatus(basicProperties.getCorrelationId(), experimentResponse);
                handleExperimentResponse(experimentResponse, ecaServiceTrack);
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                showFormattedErrorMessageDialog(JMainFrame.this, e.getMessage());
            }
        };
    }

    private void handleExperimentResponse(ExperimentResponse experimentResponse, EcaServiceTrack ecaServiceTrack) {
        experimentResponse.getStatus().handle(new TechnicalStatusVisitor() {
            @Override
            public void caseSuccessStatus() {
                int result = JOptionPane.showConfirmDialog(JMainFrame.this,
                        String.format(EXPERIMENT_FINISHED_MESSAGE_TEXT_FORMAT,
                                ecaServiceTrack.getDetails()), null, JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        Objects.requireNonNull(experimentResponse.getDownloadUrl(),
                                "Can't load experiment for null url");
                        URL experimentUrl = new URL(experimentResponse.getDownloadUrl());
                        ExperimentLoader loader = new ExperimentLoader(new UrlResource(experimentUrl));
                        processExperimentLoading(loader);
                    } catch (Exception ex) {
                        LoggerUtils.error(log, ex);
                        showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                    }
                }
            }

            @Override
            public void caseInProgressStatus() {
                JOptionPane.showMessageDialog(JMainFrame.this,
                        String.format(EXPERIMENT_SUCCESS_MESSAGE_FORMAT, ecaServiceTrack.getDetails()),
                        null, JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void caseErrorStatus() {
                showFormattedErrorMessageDialog(JMainFrame.this, getFirstErrorAsString(experimentResponse));
            }

            @Override
            public void caseTimeoutStatus() {
                JOptionPane.showMessageDialog(JMainFrame.this, EXPERIMENT_TIMEOUT_MESSAGE, null,
                        JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void caseValidationErrorStatus() {
                showValidationErrorsDialog(JMainFrame.this, experimentResponse);
            }

            @Override
            public void caseCanceledStatus() {
                JOptionPane.showMessageDialog(JMainFrame.this, EXPERIMENT_CANCEL_MESSAGE, null,
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private EcaServiceTrack getEcaServiceTrack(String correlationId) {
        return ecaServiceTrackFrame.getEcaServiceTrackTable().getTrack(correlationId);
    }

    private ActionListener experimentRequestActionListener() {
        return new ActionListener() {

            ExperimentRequestDto experimentRequestDto;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (Boolean.FALSE.equals(CONFIG_SERVICE.getEcaServiceConfig().getEnabled())) {
                    JOptionPane.showMessageDialog(JMainFrame.this, ECA_SERVICE_DISABLED_MESSAGE, null,
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    performTaskWithDataAndAttributesValidation(() -> {
                        final DataBuilder dataBuilder = new DataBuilder();
                        prepareTrainingData(dataBuilder, () -> {
                            ExperimentRequestDialog experimentRequestDialog =
                                    new ExperimentRequestDialog(JMainFrame.this);
                            experimentRequestDialog.showDialog(experimentRequestDto);
                            if (experimentRequestDialog.isDialogResult()) {
                                experimentRequestDto = experimentRequestDialog.createExperimentRequestDto();
                                String dataUuid = uploadInstancesCacheService.uploadInstances(dataBuilder.getResult());
                                experimentRequestDto.setDataUuid(dataUuid);
                                String correlationId = UUID.randomUUID().toString();
                                EcaServiceTrack ecaServiceTrack = EcaServiceTrack.builder()
                                        .correlationId(correlationId)
                                        .requestType(EcaServiceRequestType.EXPERIMENT)
                                        .status(EcaServiceTrackStatus.READY)
                                        .details(experimentRequestDto.getExperimentType().getDescription())
                                        .relationName(dataBuilder.getResult().getData().relationName())
                                        .build();
                                addEcaServiceTrack(ecaServiceTrack);
                                try {
                                    rabbitClient.sendExperimentRequest(experimentRequestDto, experimentQueue,
                                            correlationId);
                                    updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.REQUEST_SENT);
                                    String infoMessage = String.format(ECA_SERVICE_EXPERIMENT_REQUEST_SENT_MESSAGE,
                                            ecaServiceTrack.getDetails());
                                    popupService.showInfoPopup(infoMessage, JMainFrame.this);
                                } catch (Exception ex) {
                                    LoggerUtils.error(log, ex);
                                    updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.ERROR);
                                    showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                                }
                            }
                            experimentRequestDialog.dispose();
                        });
                    });
                }
            }
        };
    }

    private ActionListener optimalClassifierActionListener() {
        return event -> {
            if (Boolean.FALSE.equals(CONFIG_SERVICE.getEcaServiceConfig().getEnabled())) {
                JOptionPane.showMessageDialog(JMainFrame.this, ECA_SERVICE_DISABLED_MESSAGE, null,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        String correlationId = UUID.randomUUID().toString();
                        EcaServiceTrack ecaServiceTrack = EcaServiceTrack.builder()
                                .correlationId(correlationId)
                                .requestType(EcaServiceRequestType.OPTIMAL_CLASSIFIER)
                                .status(EcaServiceTrackStatus.READY)
                                .relationName(dataBuilder.getResult().getData().relationName())
                                .build();
                        addEcaServiceTrack(ecaServiceTrack);
                        try {
                            String dataUuid = uploadInstancesCacheService.uploadInstances(dataBuilder.getResult());
                            rabbitClient.sendEvaluationRequest(dataUuid, evaluationQueue, correlationId);
                            updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.REQUEST_SENT);
                            popupService.showInfoPopup(ECA_SERVICE_OPTIMAL_CLASSIFIER_REQUEST_SENT_MESSAGE, this);
                        } catch (Exception ex) {
                            LoggerUtils.error(log, ex);
                            updateEcaServiceTrackStatus(correlationId, EcaServiceTrackStatus.ERROR);
                            showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                        }
                    });
                });
            }
        };
    }

    private ActionListener contingencyTableActionListener() {
        return event ->
                performTaskWithDataAndAttributesValidation(() -> {
                    final DataBuilder dataBuilder = new DataBuilder();
                    prepareTrainingData(dataBuilder, () -> {
                        ContingencyTableOptionsDialog contingencyTableOptionsDialog = new
                                ContingencyTableOptionsDialog(JMainFrame.this, dataBuilder.getResult().getData());
                        contingencyTableOptionsDialog.setVisible(true);
                        if (contingencyTableOptionsDialog.isDialogResult()) {
                            int rowAttrIndex = contingencyTableOptionsDialog.gerRowAttributeIndex();
                            int colAttrIndex = contingencyTableOptionsDialog.gerColAttributeIndex();
                            ContingencyTable contingencyTable = new ContingencyTable(dataBuilder.getResult().getData());
                            contingencyTable.setAlpha(contingencyTableOptionsDialog.getAlpha());
                            contingencyTable.setUseYates(contingencyTableOptionsDialog.isUseYates());
                            ContingencyTableAction contingencyTableAction =
                                    new ContingencyTableAction(contingencyTable, rowAttrIndex, colAttrIndex);
                            LoadDialog progress = new LoadDialog(JMainFrame.this, contingencyTableAction,
                                    CONTINGENCY_TABLE_LOADING_MESSAGE);

                            processAsyncTask(progress, () -> {
                                Attribute rowAttribute = dataBuilder.getResult().getData().attribute(rowAttrIndex);
                                Attribute colAttribute = dataBuilder.getResult().getData().attribute(colAttrIndex);
                                DecimalFormat decimalFormat = NumericFormatFactory.getInstance();
                                decimalFormat.setMaximumFractionDigits(maximumFractionDigits);
                                ContingencyTableReportModel reportModel = new ContingencyTableReportModel();
                                reportModel.setRowAttribute(rowAttribute);
                                reportModel.setColAttribute(colAttribute);
                                reportModel.setContingencyMatrix(contingencyTableAction.getContingencyMatrix());
                                reportModel.setChiSquareTestResult(contingencyTableAction.getResult());
                                reportModel.setDecimalFormat(decimalFormat);
                                ContingencyTableResultFrame contingencyTableResultFrame =
                                        new ContingencyTableResultFrame(JMainFrame.this, reportModel);
                                contingencyTableResultFrame.setVisible(true);
                            });
                        }
                        contingencyTableOptionsDialog.dispose();
                    });
                });
    }

    private ActionListener openFileActionListener() {
        return new ActionListener() {

            FileDataLoader dataLoader = new FileDataLoader();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    OpenDataFileChooser fileChooser = SingletonRegistry.getSingleton(OpenDataFileChooser.class);
                    File file = fileChooser.openFile(JMainFrame.this);
                    if (file != null) {
                        dataLoader.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                        dataLoader.setSource(new FileResource(file));
                        InstancesLoader loader = new InstancesLoader(dataLoader);
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader, DATA_LOADING_MESSAGE);
                        processAsyncTask(progress, () -> createDataFrame(loader.getResult()));
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener saveFileActionListener() {
        return new ActionListener() {

            FileDataSaver dataSaver = new FileDataSaver();

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (isDataValid()) {
                        final DataBuilder dataBuilder = new DataBuilder(false);
                        prepareTrainingData(dataBuilder, () -> {
                            SaveDataFileChooser fileChooser = SingletonRegistry.getSingleton(SaveDataFileChooser.class);
                            fileChooser.setSelectedFile(new File(dataBuilder.getResult().getData().relationName()));
                            File file = fileChooser.getSelectedFile(JMainFrame.this);
                            if (file != null) {
                                dataSaver.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                                CallbackAction action =
                                        () -> dataSaver.saveData(file, dataBuilder.getResult().getData());
                                LoadDialog loadDialog = new LoadDialog(JMainFrame.this,
                                        action, SAVE_DATA_TITLE, false);
                                processAsyncTask(loadDialog, () -> {
                                });
                            }
                        });
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(JMainFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }

    private ActionListener dbConnectionActionListener() {
        return event -> {
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

                    processAsyncTask(progress, () -> {
                        QueryFrame queryFrame = new QueryFrame(JMainFrame.this, connection);
                        queryFrame.setVisible(true);
                    });

                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                }
            }
            conn.dispose();
        };
    }

    private ActionListener dbSaverActionListener() {
        return event -> {
            if (isDataValid()) {
                try {
                    final DataBuilder dataBuilder = new DataBuilder(false);
                    prepareTrainingData(dataBuilder, () -> {
                        DatabaseSaverDialog databaseSaverDialog = new DatabaseSaverDialog(JMainFrame.this);
                        databaseSaverDialog.setTableName(dataBuilder.getResult().getData().relationName());
                        databaseSaverDialog.setVisible(true);
                        if (databaseSaverDialog.dialogResult()) {
                            DatabaseSaver databaseSaver =
                                    new DatabaseSaver(databaseSaverDialog.getConnectionDescriptor());
                            databaseSaver.setTableName(databaseSaverDialog.getTableName());
                            LoadDialog progress = new LoadDialog(JMainFrame.this,
                                    new DatabaseSaverAction(databaseSaver, dataBuilder.getResult().getData()),
                                    DB_SAVE_PROGRESS_MESSAGE_TEXT, false);
                            processAsyncTask(progress, () ->
                                    JOptionPane.showMessageDialog(JMainFrame.this,
                                            String.format(SAVE_DATA_INFO_FORMAT, databaseSaver.getTableName()), null,
                                            JOptionPane.INFORMATION_MESSAGE)
                            );
                        }
                        databaseSaverDialog.dispose();
                    });

                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                }
            }
        };
    }

    private ActionListener urlLoaderActionListener() {
        return event -> {
            String dataUrl = (String) JOptionPane.showInputDialog(JMainFrame.this,
                    URL_FILE_TEXT, LOAD_DATA_FROM_NET_TITLE, JOptionPane.INFORMATION_MESSAGE, null,
                    null, DEFAULT_URL_FOR_DATA_LOADING);
            if (dataUrl != null) {
                if (!isValidUrl(dataUrl)) {
                    showFormattedErrorMessageDialog(JMainFrame.this, INVALID_FILE_URL_MESSAGE);
                } else {
                    try {
                        FileDataLoader dataLoader = new FileDataLoader();
                        dataLoader.setSource(new UrlResource(new URL(dataUrl.trim())));
                        dataLoader.setDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                        UrlLoader loader = new UrlLoader(dataLoader);
                        LoadDialog progress = new LoadDialog(JMainFrame.this,
                                loader, DATA_LOADING_MESSAGE);
                        processAsyncTask(progress, () -> createDataFrame(loader.getResult()));
                    } catch (Exception ex) {
                        LoggerUtils.error(log, ex);
                        showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                    }
                }
            }
        };
    }

    private ActionListener loadModelActionListener() {
        return event -> {
            try {
                OpenModelChooser fileChooser = SingletonRegistry.getSingleton(OpenModelChooser.class);
                File file = fileChooser.openFile(JMainFrame.this);
                if (file != null) {
                    ClassifierModelLoader loader = new ClassifierModelLoader(new FileResource(file));
                    LoadDialog progress = new LoadDialog(JMainFrame.this,
                            loader, MODEL_LOADING_MESSAGE);

                    processAsyncTask(progress, () -> {
                        ClassificationModel classificationModel = loader.getResult();
                        int digits = Optional.ofNullable(classificationModel.getMaximumFractionDigits())
                                .orElse(maximumFractionDigits);
                        String title = getClassifierName(classificationModel.getClassifier());
                        createEvaluationResultsAsync(title, new ReferenceWrapper<>(classificationModel.getClassifier()),
                                classificationModel.getEvaluation().getData(), classificationModel.getEvaluation(),
                                digits);
                    });

                }
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
                showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
            }
        };
    }

    private ActionListener loadExperimentFromFileActionListener() {
        return event -> {
            try {
                OpenModelChooser fileChooser = SingletonRegistry.getSingleton(OpenModelChooser.class);
                File file = fileChooser.openFile(JMainFrame.this);
                if (file != null) {
                    ExperimentLoader loader = new ExperimentLoader(new FileResource(file));
                    processExperimentLoading(loader);
                }
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
                showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
            }
        };
    }

    private ActionListener loadExperimentFromUrlActionListener() {
        return event -> {
            String url = (String) JOptionPane.showInputDialog(JMainFrame.this,
                    EXPERIMENT_URL_FILE_TEXT, DOWNLOAD_EXPERIMENT_TITLE, JOptionPane.INFORMATION_MESSAGE, null,
                    null, null);
            if (url != null) {
                if (!isValidUrl(url)) {
                    showFormattedErrorMessageDialog(JMainFrame.this, INVALID_FILE_URL_MESSAGE);
                } else {
                    try {
                        URL experimentUrl = new URL(url.trim());
                        ExperimentLoader loader = new ExperimentLoader(new UrlResource(experimentUrl));
                        processExperimentLoading(loader);
                    } catch (Exception ex) {
                        LoggerUtils.error(log, ex);
                        showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                    }
                }
            }
        };
    }

    private ActionListener dataGeneratorActionListener() {
        return event -> {
            DataGeneratorDialog dialog = new DataGeneratorDialog(JMainFrame.this);
            dialog.setVisible(true);
            if (dialog.dialogResult()) {
                try {
                    DataGeneratorCallback loader = new DataGeneratorCallback(dialog.getDataGenerator());
                    LoadDialog progress = new LoadDialog(JMainFrame.this, loader,
                            DATA_GENERATION_LOADING_MESSAGE);
                    processAsyncTask(progress, () -> createDataFrame(loader.getResult(), maximumFractionDigits));
                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    showFormattedErrorMessageDialog(JMainFrame.this, ex.getMessage());
                }
            }
            dialog.dispose();
        };
    }

    private void processExperimentLoading(ExperimentLoader loader) throws Exception {
        LoadDialog loadDialog = new LoadDialog(JMainFrame.this,
                loader, EXPERIMENT_LOADING_MESSAGE);
        processAsyncTask(loadDialog, () -> {
            AbstractExperiment<?> experiment = loader.getResult();
            ExperimentFrame<?> experimentFrame =
                    ExperimentFrameFactory.getExperimentFrame(experiment, JMainFrame.this,
                            this.maximumFractionDigits);
            experimentFrame.setVisible(true);
        });
    }

    private ClassificationModel downloadModel(EvaluationResponse evaluationResponse) throws IOException {
        URL modelUrl = new URL(evaluationResponse.getModelUrl());
        UrlResource urlResource = new UrlResource(modelUrl);
        return ModelSerializationHelper.deserialize(urlResource, ClassificationModel.class);
    }

    private void addEcaServiceTrack(EcaServiceTrack ecaServiceTrack) {
        ecaServiceTrackFrame.getEcaServiceTrackTable().addTrack(ecaServiceTrack);
    }

    private void updateEcaServiceTrackStatus(String correlationId, EcaServiceTrackStatus status) {
        ecaServiceTrackFrame.getEcaServiceTrackTable().updateTrackStatus(correlationId, status);
    }
}
