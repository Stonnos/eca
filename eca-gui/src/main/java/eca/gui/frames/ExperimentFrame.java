package eca.gui.frames;

import eca.config.ConfigurationService;
import eca.config.registry.SingletonRegistry;
import eca.core.ModelSerializationHelper;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.resource.FileResource;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.ExperimentHistoryMode;
import eca.dataminer.IterativeExperiment;
import eca.gui.PanelBorderUtils;
import eca.gui.actions.CallbackAction;
import eca.gui.actions.ExperimentLoader;
import eca.gui.choosers.OpenModelChooser;
import eca.gui.choosers.SaveModelChooser;
import eca.gui.dialogs.EvaluationMethodOptionsDialog;
import eca.gui.dialogs.LoadDialog;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.logging.LoggerUtils;
import eca.gui.service.ClassifierIndexerService;
import eca.gui.service.ExecutorService;
import eca.gui.service.ExperimentNamesFactory;
import eca.gui.tables.ExperimentTable;
import eca.report.ReportGenerator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import static eca.gui.GuiUtils.removeComponents;
import static eca.gui.GuiUtils.showFormattedErrorMessageDialog;

/**
 * Implements basic experiment frame.
 *
 * @author Roman Batygin
 */
@Slf4j
public abstract class ExperimentFrame<T extends AbstractExperiment<?>> extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String BUILDING_PROGRESS_TITLE = "Пожалуйста подождите, идет построение моделей...";
    private static final String LOAD_EXPERIMENT_TITLE = "Пожалуйста подождите, идет загрузка истории эксперимента...";
    private static final String SAVE_EXPERIMENT_TITLE = "Пожалуйста подождите, идет сохранение истории эксперимента...";
    private static final String EXPERIMENT_HISTORY_TITLE = "История эксперимента";
    private static final String INFO_TITLE = "Информация";
    private static final String START_BUTTON_TEXT = "Начать эксперимент";
    private static final String STOP_BUTTON_TEXT = "Остановить эксперимент";
    private static final String OPTIONS_BUTTON_TEXT = "Настройки";
    private static final String SAVE_BUTTON_TEXT = "Сохранить эксперимент";
    private static final String LOAD_BUTTON_TEXT = "Загрузить эксперимент";
    private static final String INITIAL_DATA_BUTTON_TEXT = "Исходные данные";

    private static final Font TEXT_AREA_FONT = new Font("Arial", Font.BOLD, 13);
    private static final int DEFAULT_WIDTH = 1100;
    private static final int DEFAULT_HEIGHT = 725;
    private static final String START_TIME_TEXT = "00:00:00:000";
    private static final int TIMER_FIELD_LENGTH = 20;
    private static final String TIMER_LABEL_TEXT = "Время выполнения эксперимента";
    private static final int TIMER_DELAY_IN_MILLIS = 1;
    private static final int EXPERIMENT_RESULTS_FONT_SIZE = 12;
    private static final Dimension RESULTS_PANE_PREFERRED_SIZE = new Dimension(1000, 325);
    private static final String PROGRESS_TITLE_FORMAT =
            "<html><body><span style = 'font-weight: bold; font-family: \"Arial\"; font-size: %d'>%s</span></body></html>";
    private static final String TEXT_HTML = "text/html";
    private static final String INVALID_EXPERIMENT_TYPE_MESSAGE_FORMAT =
            "Загружен недопустимый эксперимент '%s'. Допускается загрузка эксперимента типа '%s'!";
    private static final String EMPTY_HISTORY_ERROR_MESSAGE = "Невозможно сохранить пустую историю эксперимета!";
    private static final String EXPERIMENT_HISTORY_OPTIONS_TITLE = "Настройки истории эксперимента";
    private static final String FULL_HISTORY_TITLE = "Полная история";
    private static final String BEST_MODELS_TITLE = "Только оптимальные модели";
    private static final String ONLY_BEST_MODELS_TOOLTIP_TEXT = "Отбор только оптимальных моделей";
    private static final String FULL_HISTORY_TOOLTIP_TEXT = "Построение полной истории эксперимента";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
    private final long experimentId = System.currentTimeMillis();

    @Getter
    private final Class<T> experimentClass;
    @Getter
    private final int digits;
    @Getter
    private T experiment;

    private JProgressBar experimentProgressBar;
    private JTextPane experimentResultsPane;
    private JRadioButton useTrainingSet;
    private JRadioButton useTestingSet;
    private JRadioButton fullHistoryRadioButton;
    private JRadioButton onlyBestModelsRadioButton;
    private JSpinner foldsSpinner = new JSpinner();
    private JSpinner validationsSpinner = new JSpinner();

    private JButton startButton;
    private JButton optionsButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton stopButton;
    private JPanel evaluationMethodPanel;
    private JPanel experimentHistoryModePanel;

    private JTextField timerField;

    private ExperimentTable experimentTable;
    private SwingWorker<Void, Void> worker;
    private SwingWorker<Void, Void> timer;

    private InstancesFrame dataFrame;

    private volatile boolean disposed;

    protected ExperimentFrame(Class<T> experimentClass, T experiment, JFrame parent, int digits) {
        Objects.requireNonNull(experimentClass, "Expected not null experiment class");
        Objects.requireNonNull(experiment, "Expected not null experiment");
        this.experimentClass = experimentClass;
        this.experiment = experiment;
        this.digits = digits;
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.setIconImage(parent.getIconImage());
        this.createGUI();
        this.displayResults(experiment);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void dispose() {
        disposed = true;
        if (worker != null && !worker.isCancelled()) {
            worker.cancel(true);
        }
        worker = null;
        experimentTable.clearAndResetExperimentModel();
        experimentResultsPane = null;
        experiment = null;
        if (dataFrame != null) {
            dataFrame.dispose();
        }
        removeComponents(this);
        super.dispose();
    }

    private void setStateForButtons(boolean flag) {
        startButton.setEnabled(flag);
        optionsButton.setEnabled(flag);
        loadButton.setEnabled(flag);
        saveButton.setEnabled(flag);
        stopButton.setEnabled(!flag);
    }

    private void setStateForOptions(boolean flag) {
        for (Component c : evaluationMethodPanel.getComponents()) {
            c.setEnabled(flag);
        }
        for (Component c : experimentHistoryModePanel.getComponents()) {
            c.setEnabled(flag);
        }
        if (flag && useTrainingSet.isSelected()) {
            foldsSpinner.setEnabled(false);
            validationsSpinner.setEnabled(false);
        }
    }

    private void displayResults(T experimentHistory) {
        if (experimentHistory.getHistory() != null && !experimentHistory.getHistory().isEmpty()) {
            experimentTable.setRenderer(Color.RED);
            experimentResultsPane.setText(ReportGenerator.getExperimentResultsAsHtml(experimentHistory,
                    CONFIG_SERVICE.getApplicationConfig().getExperimentConfig().getNumBestResults()));
            experimentResultsPane.setCaretPosition(0);
        }
    }

    protected abstract void initializeExperimentOptions();

    protected void doBegin() {
        experimentProgressBar.setValue(0);
        worker = new ExperimentWorker();
        timer = new TimeWorker();
    }

    private void createEvaluationMethodPanel() {
        evaluationMethodPanel = new JPanel(new GridBagLayout());
        evaluationMethodPanel.setBorder(
                PanelBorderUtils.createTitledBorder(EvaluationMethodOptionsDialog.METHOD_TITLE));
        ButtonGroup group = new ButtonGroup();
        useTrainingSet = new JRadioButton(EvaluationMethod.TRAINING_DATA.getDescription());
        useTestingSet = new JRadioButton(EvaluationMethod.CROSS_VALIDATION.getDescription());
        group.add(useTrainingSet);
        group.add(useTestingSet);
        foldsSpinner.setModel(
                new SpinnerNumberModel(experiment.getNumFolds(), CommonDictionary.MINIMUM_NUMBER_OF_FOLDS,
                        CommonDictionary.MAXIMUM_NUMBER_OF_FOLDS, 1));
        validationsSpinner.setModel(
                new SpinnerNumberModel(experiment.getNumTests(), CommonDictionary.MINIMUM_NUMBER_OF_TESTS,
                        CommonDictionary.MAXIMUM_NUMBER_OF_TESTS, 1));
        foldsSpinner.setEnabled(false);
        validationsSpinner.setEnabled(false);

        switch (getExperiment().getEvaluationMethod()) {
            case TRAINING_DATA:
                useTrainingSet.setSelected(true);
                break;
            case CROSS_VALIDATION:
                useTestingSet.setSelected(true);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unexpected evaluation method [%s]!", getExperiment().getEvaluationMethod()));
        }

        useTestingSet.addItemListener(e -> {
            foldsSpinner.setEnabled(useTestingSet.isSelected());
            validationsSpinner.setEnabled(useTestingSet.isSelected());
        });

        evaluationMethodPanel.add(useTrainingSet, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        evaluationMethodPanel.add(useTestingSet, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        evaluationMethodPanel.add(new JLabel(EvaluationMethodOptionsDialog.BLOCKS_NUM_TITLE),
                new GridBagConstraints(0, 2, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        evaluationMethodPanel.add(foldsSpinner, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        evaluationMethodPanel.add(new JLabel(EvaluationMethodOptionsDialog.TESTS_NUM_TITLE),
                new GridBagConstraints(0, 3, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        evaluationMethodPanel.add(validationsSpinner, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));

    }

    private void createExperimentHistoryModePanel() {
        experimentHistoryModePanel = new JPanel(new GridBagLayout());
        experimentHistoryModePanel.setBorder(
                PanelBorderUtils.createTitledBorder(EXPERIMENT_HISTORY_OPTIONS_TITLE));
        ButtonGroup group = new ButtonGroup();
        fullHistoryRadioButton = new JRadioButton(FULL_HISTORY_TITLE);
        fullHistoryRadioButton.setToolTipText(FULL_HISTORY_TOOLTIP_TEXT);
        onlyBestModelsRadioButton = new JRadioButton(BEST_MODELS_TITLE);
        onlyBestModelsRadioButton.setToolTipText(ONLY_BEST_MODELS_TOOLTIP_TEXT);
        group.add(fullHistoryRadioButton);
        group.add(onlyBestModelsRadioButton);
        onlyBestModelsRadioButton.setSelected(true);
        experimentHistoryModePanel.add(fullHistoryRadioButton, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        experimentHistoryModePanel.add(onlyBestModelsRadioButton, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));

    }

    private JScrollPane createExperimentResultsAsTextPanel() {
        experimentResultsPane = new JTextPane();
        experimentResultsPane.setEditable(false);
        experimentResultsPane.setFont(TEXT_AREA_FONT);
        experimentResultsPane.setContentType(TEXT_HTML);
        experimentResultsPane.setPreferredSize(RESULTS_PANE_PREFERRED_SIZE);
        JScrollPane experimentResultsPanel = new JScrollPane(experimentResultsPane);
        experimentResultsPanel.setBorder(PanelBorderUtils.createTitledBorder(INFO_TITLE));
        return experimentResultsPanel;
    }

    private JScrollPane createExperimentHistoryScrollPane() {
        experimentTable = new ExperimentTable(experiment, this, digits);
        JScrollPane experimentHistoryScrollPane = new JScrollPane(experimentTable);
        experimentHistoryScrollPane.setBorder(PanelBorderUtils.createTitledBorder(EXPERIMENT_HISTORY_TITLE));
        return experimentHistoryScrollPane;
    }

    private JPanel createExperimentMenuPanel() {
        JPanel experimentMenuPanel = new JPanel(new GridBagLayout());
        JButton initialDataButton = new JButton(INITIAL_DATA_BUTTON_TEXT);
        startButton = new JButton(START_BUTTON_TEXT);
        stopButton = new JButton(STOP_BUTTON_TEXT);
        stopButton.setEnabled(false);
        optionsButton = new JButton(OPTIONS_BUTTON_TEXT);
        saveButton = new JButton(SAVE_BUTTON_TEXT);
        loadButton = new JButton(LOAD_BUTTON_TEXT);

        initialDataButton.addActionListener(e -> {
            if (dataFrame == null) {
                dataFrame = new InstancesFrame(experiment.getData(), ExperimentFrame.this);
            }
            dataFrame.setVisible(true);
        });

        startButton.addActionListener(e -> startExperiment());
        stopButton.addActionListener(e -> worker.cancel(true));
        optionsButton.addActionListener(e -> initializeExperimentOptions());
        saveButton.addActionListener(event -> saveExperimentHistory());
        loadButton.addActionListener(event -> loadExperiment());

        createTimerField();

        experimentMenuPanel.add(initialDataButton,
                new GridBagConstraints(0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
        experimentMenuPanel.add(optionsButton,
                new GridBagConstraints(0, 1, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
        experimentMenuPanel.add(startButton,
                new GridBagConstraints(0, 2, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
        experimentMenuPanel.add(stopButton,
                new GridBagConstraints(0, 3, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
        experimentMenuPanel.add(loadButton,
                new GridBagConstraints(0, 4, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
        experimentMenuPanel.add(saveButton,
                new GridBagConstraints(0, 5, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
        experimentMenuPanel.add(new JLabel(TIMER_LABEL_TEXT),
                new GridBagConstraints(0, 6, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(25, 5, 5, 5), 0, 0));
        experimentMenuPanel.add(timerField,
                new GridBagConstraints(0, 7, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 10, 5), 0, 0));
        return experimentMenuPanel;
    }

    private void createTimerField() {
        timerField = new JTextField(TIMER_FIELD_LENGTH);
        timerField.setEditable(false);
        timerField.setText(START_TIME_TEXT);
        timerField.setBackground(Color.WHITE);
        timerField.setHighlighter(null);
    }

    private void createGUI() {
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setLayout(new GridBagLayout());
        createEvaluationMethodPanel();
        createExperimentHistoryModePanel();
        createExperimentProgressBar();
        JPanel mainTopPanel = new JPanel(new GridBagLayout());

        mainTopPanel.add(evaluationMethodPanel,
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        mainTopPanel.add(experimentHistoryModePanel,
                new GridBagConstraints(0, 1, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        mainTopPanel.add(createExperimentMenuPanel(),
                new GridBagConstraints(0, 2, 1, 1, 0, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        mainTopPanel.add(createExperimentHistoryScrollPane(),
                new GridBagConstraints(1, 0, 1, 3, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        this.add(mainTopPanel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(createExperimentResultsAsTextPanel(),
                new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(experimentProgressBar,
                new GridBagConstraints(0, 2, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
        this.getRootPane().setDefaultButton(startButton);
    }

    private void startExperiment() {
        experiment.setEvaluationMethod(useTestingSet.isSelected()
                ? EvaluationMethod.CROSS_VALIDATION : EvaluationMethod.TRAINING_DATA);
        experiment.setExperimentHistoryMode(onlyBestModelsRadioButton.isSelected()
                ? ExperimentHistoryMode.ONLY_BEST_MODELS : ExperimentHistoryMode.FULL);
        experiment.setNumBestResults(CONFIG_SERVICE.getApplicationConfig().getExperimentConfig().getNumBestResults());
        if (useTestingSet.isSelected()) {
            experiment.setNumFolds(((SpinnerNumberModel) foldsSpinner.getModel()).getNumber().intValue());
            experiment.setNumTests(
                    ((SpinnerNumberModel) validationsSpinner.getModel()).getNumber().intValue());
        }
        experimentResultsPane.setText(
                String.format(PROGRESS_TITLE_FORMAT, EXPERIMENT_RESULTS_FONT_SIZE, BUILDING_PROGRESS_TITLE));
        setStateForButtons(false);
        setStateForOptions(false);
        experimentTable.setRenderer(Color.BLACK);
        experimentTable.clear();
        doBegin();
        worker.execute();
        timer.execute();
        log.info("Starting experiment with id {} for classifier '{}'.", experimentId,
                experiment.getClassifier().getClass().getSimpleName());
    }

    private void saveExperimentHistory() {
        try {
            AbstractExperiment<?> experimentHistory = experimentTable.experimentModel().getExperiment();
            if (experimentHistory == null || experimentHistory.getHistory().isEmpty()) {
                JOptionPane.showMessageDialog(ExperimentFrame.this, EMPTY_HISTORY_ERROR_MESSAGE, null,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                SaveModelChooser fileChooser = SingletonRegistry.getSingleton(SaveModelChooser.class);
                fileChooser.setSelectedFile(
                        new File(ClassifierIndexerService.getExperimentIndex(experiment.getClassifier())));
                File file = fileChooser.getSelectedFile(ExperimentFrame.this);
                if (file != null) {
                    internalSaveExperimentHistory(file);
                }
            }
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(ExperimentFrame.this, e.getMessage(),
                    null, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void internalSaveExperimentHistory(File file) throws Exception {
        CallbackAction action = () -> ModelSerializationHelper.serialize(file, experiment);
        LoadDialog loadDialog = new LoadDialog(ExperimentFrame.this,
                action, SAVE_EXPERIMENT_TITLE, false);
        ExecutorService.process(loadDialog, ExperimentFrame.this);
    }

    private void loadExperiment() {
        OpenModelChooser fileChooser = SingletonRegistry.getSingleton(OpenModelChooser.class);
        File file = fileChooser.openFile(ExperimentFrame.this);
        if (file != null) {
            try {
                ExperimentLoader loader = new ExperimentLoader(new FileResource(file));
                LoadDialog loadDialog = new LoadDialog(ExperimentFrame.this,
                        loader, LOAD_EXPERIMENT_TITLE);

                ExecutorService.process(loadDialog, () -> {
                    AbstractExperiment<?> loaderExperiment = loader.getResult();
                    String loadedExperimentAlg = loaderExperiment.getClassifier().getClass().getSimpleName();
                    String currentExperimentAlg = experiment.getClassifier().getClass().getSimpleName();
                    if (!experiment.getClass().equals(loaderExperiment.getClass())
                            || !currentExperimentAlg.equals(loadedExperimentAlg)) {
                        String loadedExperimentName = ExperimentNamesFactory.getExperimentName(loaderExperiment);
                        String actualExperimentName = getTitle();
                        String errorMessage =
                                String.format(INVALID_EXPERIMENT_TYPE_MESSAGE_FORMAT, loadedExperimentName,
                                        actualExperimentName);
                        showFormattedErrorMessageDialog(ExperimentFrame.this, errorMessage);
                    } else {
                        experiment = experimentClass.cast(loaderExperiment);
                        experimentTable.initializeExperimentHistory(experiment);
                        displayResults(experimentClass.cast(experiment));
                    }
                }, () -> showFormattedErrorMessageDialog(ExperimentFrame.this, loadDialog.getErrorMessageText()));

            } catch (Exception e) {
                LoggerUtils.error(log, e);
                showFormattedErrorMessageDialog(ExperimentFrame.this, e.getMessage());
            }
        }
    }

    private void createExperimentProgressBar() {
        experimentProgressBar = new JProgressBar();
        experimentProgressBar.setStringPainted(true);
    }

    private class TimeWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            Date startTime = new Date();
            while (!worker.isDone()) {
                try {
                    Thread.sleep(TIMER_DELAY_IN_MILLIS);
                    long currentTimeMillis = new Date().getTime() - startTime.getTime();
                    timerField.setText(dateFormat.format(new Date(currentTimeMillis)));
                } catch (InterruptedException ex) {
                    LoggerUtils.error(log, ex);
                    Thread.currentThread().interrupt();
                }
            }
            return null;
        }

    }

    /**
     * Experiment worker.
     */
    private class ExperimentWorker extends SwingWorker<Void, Void> {

        static final String PROGRESS_PROPERTY = "progress";

        IterativeExperiment object;
        boolean error;

        ExperimentWorker() {
            object = getExperiment().getIterativeExperiment();
            this.addPropertyChangeListener(evt -> {
                if (PROGRESS_PROPERTY.equals(evt.getPropertyName())) {
                    experimentProgressBar.setValue((Integer) evt.getNewValue());
                }
            });

        }

        @Override
        protected Void doInBackground() {
            try {
                while (!isCancelled() && object.hasNext()) {
                    performNextIteration();
                    updateProgress();
                }
            } catch (Throwable e) {
                LoggerUtils.error(log, e);
                error = true;
                experimentResultsPane.setText(e.toString());
            }
            return null;
        }

        @Override
        protected void done() {
            setProgress(100);
            setStateForButtons(true);
            setStateForOptions(true);
            if (!disposed) {
                if (!error) {
                    experimentTable.sortByBestResults();
                    displayResults(experiment);
                    log.info("Experiment {} has been successfully finished for classifier '{}'.", experimentId,
                            experiment.getClassifier().getClass().getSimpleName());
                }
                System.gc();
            } else {
                experimentTable.clear();
            }
        }

        private void performNextIteration() {
            try {
                object.next();
                if (!isCancelled()) {
                    experimentTable.notifyLastInsertedResults();
                }
            } catch (Exception e) {
                LoggerUtils.error(log, e);
            }
        }

        private void updateProgress() {
            if (!isCancelled()) {
                int percent = object.getPercent();
                if (percent != getProgress() && percent % 10 == 0) {
                    log.info("Experiment {} progress: {} %.", experimentId, percent);
                }
                setProgress(percent);
            }
        }

    }
}
