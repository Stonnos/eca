/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.*;

import eca.beans.ClassifierDescriptor;
import eca.core.TestMethod;
import eca.core.converters.ModelConverter;
import eca.dataminer.IterativeExperiment;
import eca.gui.ExecutorService;
import eca.gui.PanelBorderUtils;
import eca.dataminer.AbstractExperiment;
import eca.core.ClassifierIndexer;
import eca.gui.actions.CallbackAction;
import eca.gui.dialogs.LoadDialog;
import eca.gui.dialogs.TestingSetOptionsDialog;
import eca.gui.tables.ExperimentTable;
import eca.gui.choosers.SaveModelChooser;
import eca.gui.choosers.OpenModelChooser;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import eca.dataminer.ExperimentHistory;

/**
 *
 * @author Roman93
 */
public abstract class ExperimentFrame extends JFrame {

    private static final String buildingProgressTitle = "Пожалуйста подождите, идет построение моделей...";

    private static final String loadExperimentTitle = "Пожалуйста подождите, идет загрузка истории эксперимента...";

    private final AbstractExperiment experiment;

    protected JProgressBar progress;
    protected JTextArea text;
    private JRadioButton useTrainingSet;
    private JRadioButton useTestingSet;
    private JSpinner foldsSpinner = new JSpinner();
    private JSpinner validationsSpinner = new JSpinner();

    private JButton startButton;
    private JButton optionsButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton stopButton;
    private JPanel left;

    protected ExperimentTable table;
    protected SwingWorker<Void, Void> worker;
    private final int digits;

    protected ExperimentFrame(AbstractExperiment experiment, JFrame parent, int digits) throws Exception {
        this.experiment = experiment;
        this.digits = digits;
        this.setIconImage(parent.getIconImage());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                if (worker != null && !worker.isCancelled()) {
                    worker.cancel(true);
                }
            }
        });
        this.makeGUI();
        this.setLocationRelativeTo(parent);
    }

    public final AbstractExperiment getExperiment() {
        return experiment;
    }

    public final void setStateForButtons(boolean flag) {
        startButton.setEnabled(flag);
        optionsButton.setEnabled(flag);
        loadButton.setEnabled(flag);
        saveButton.setEnabled(flag);
        stopButton.setEnabled(!flag);
    }

    public final void setStateForOptions(boolean flag) {
        for (Component c : left.getComponents()) {
            c.setEnabled(flag);
        }
        if (flag && useTrainingSet.isSelected()) {
            foldsSpinner.setEnabled(false);
            validationsSpinner.setEnabled(false);
        }
    }


    public final String getDataInfo(Instances data) {
        StringBuilder str = new StringBuilder();
        str.append("Данные: ").append(data.relationName()).append("\n");
        str.append("Число объектов: ").append(data.numInstances()).append("\n");
        str.append("Число атрибутов: ").append(data.numAttributes()).append("\n");
        str.append("Число классов: ").append(data.numClasses()).append("\n");
        return str.toString();
    }

    public final void setDataInfo(Instances data) {
        text.setText(getDataInfo(data));
        text.setCaretPosition(0);
    }

    public final void setResults() {
        setResults(experiment.data());
    }

    public final void setResults(Instances data) {
        StringBuilder str = new StringBuilder();
        str.append("Эксперимент завершен.\n");
        str.append(getDataInfo(data));
        str.append("Метод оценки точности: ");
        switch (getExperiment().getTestMethod()) {
            case TestMethod.TRAINING_SET:
                str.append("Использование обучающего множества");
                break;
            case TestMethod.CROSS_VALIDATION:
                String s = getExperiment().getNumValidations() > 1 ? getExperiment().getNumValidations() + "*" : "";
                str.append(s).append(getExperiment().getNumFolds()).append(" - блочная кросс-проверка на тестовой выборке");
                break;
        }
        str.append("\n\n").append("Наилучшие конфигурации классификаторов:\n");
        for (int i = 0; i < Integer.min(table.getBestNumber(), table.getRowCount()); i++) {
            str.append(table.experimentModel().getClassifier(i).getClass().getSimpleName())
                    .append(" №").append(i).append("\n");
            AbstractClassifier cls = (AbstractClassifier) table.experimentModel().getClassifier(i);
            String[] options = cls.getOptions();
            for (int j = 0; j < options.length; j += 2) {
                str.append(options[j]).append(" ").append(options[j + 1]).append("\n");
            }
            str.append("\n");
        }
        text.setText(str.toString());
        text.setCaretPosition(0);
    }

    protected abstract void setOptions();

    protected void doBegin() {
        progress.setValue(0);
        worker = new SwingWorkerConstruction();
    }

    private void makeGUI() throws Exception {
        this.setSize(1100, 600);
        this.setLayout(new GridBagLayout());
        table = new ExperimentTable(new java.util.ArrayList<>(), this, experiment.data(), digits);
        JPanel top = new JPanel(new GridBagLayout());
        text = new JTextArea(10, 10);
        text.setEditable(false);
        text.setFont(new Font("Arial", Font.BOLD, 13));
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        JScrollPane bottom = new JScrollPane(text);
        bottom.setBorder(PanelBorderUtils.createTitledBorder("Информация"));
        setDataInfo(experiment.data());
        progress = new JProgressBar();
        progress.setStringPainted(true);
        //----------------------------------------------
        left = new JPanel(new GridBagLayout());
        JScrollPane right = new JScrollPane(table);
        JPanel leftBottom = new JPanel(new GridBagLayout());
        left.setBorder(PanelBorderUtils.createTitledBorder(TestingSetOptionsDialog.methodTitle));
        //-------------------------------------------------------------
        ButtonGroup group = new ButtonGroup();
        useTrainingSet = new JRadioButton(TestingSetOptionsDialog.initialMethodTitle);
        useTestingSet = new JRadioButton(TestingSetOptionsDialog.cvMethodTitle);
        group.add(useTrainingSet);
        group.add(useTestingSet);
        //---------------------------------
        foldsSpinner.setModel(new SpinnerNumberModel(experiment.getNumFolds(), 2, 100, 1));
        validationsSpinner.setModel(new SpinnerNumberModel(experiment.getNumValidations(), 1, 100, 1));
        foldsSpinner.setEnabled(false);
        validationsSpinner.setEnabled(false);
        switch (experiment.getTestMethod()) {
            case TestMethod.TRAINING_SET:
                useTrainingSet.setSelected(true);
                break;
            case TestMethod.CROSS_VALIDATION:
                useTestingSet.setSelected(true);
                break;
        }
        useTestingSet.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                foldsSpinner.setEnabled(useTestingSet.isSelected());
                validationsSpinner.setEnabled(useTestingSet.isSelected());
            }
        });
        //------------------------------------------------------
        left.add(useTrainingSet, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        left.add(useTestingSet, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        left.add(new JLabel(TestingSetOptionsDialog.blocksNumTitle), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        left.add(foldsSpinner, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        left.add(new JLabel(TestingSetOptionsDialog.validsNumTitle), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        left.add(validationsSpinner, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        //--------------------------------------------------------------
        startButton = new JButton("Начать эксперимент");
        stopButton = new JButton("Остановить эксперимент");
        stopButton.setEnabled(false);
        optionsButton = new JButton("Настройки");
        saveButton = new JButton("Сохранить эксперимент");
        loadButton = new JButton("Загрузить эксперимент");
        //---------------------------------------------------------------
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                experiment.setTestMethod(useTestingSet.isSelected()
                        ? TestMethod.CROSS_VALIDATION : TestMethod.TRAINING_SET);
                if (useTestingSet.isSelected()) {
                    experiment.setNumFolds(((SpinnerNumberModel) foldsSpinner.getModel()).getNumber().intValue());
                    experiment.setNumValidations(((SpinnerNumberModel) validationsSpinner.getModel()).getNumber().intValue());
                }
                text.setText(buildingProgressTitle);
                setStateForButtons(false);
                setStateForOptions(false);
                table.setRenderer(Color.BLACK);
                table.clear();
                doBegin();
                worker.execute();
            }
        });
        //---------------------------------------------------------------
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                worker.cancel(true);
            }
        });
        //---------------------------------------------------------------
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setOptions();
            }
        });
        //---------------------------------------------------------------
        saveButton.addActionListener(new ActionListener() {

            SaveModelChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new SaveModelChooser();
                    }
                    ClassifierIndexer indexer = new ClassifierIndexer();
                    fileChooser.setSelectedFile(new File(indexer.getExperimentIndex(experiment.getClassifier())));
                    File file = fileChooser.saveFile(ExperimentFrame.this);
                    if (file != null) {
                        ModelConverter.saveModel(file,
                                new ExperimentHistory(table.experimentModel().getExperiment(), experiment.data()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ExperimentFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //---------------------------------------------------------------
        loadButton.addActionListener(new ActionListener() {

            OpenModelChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (fileChooser == null) {
                    fileChooser = new OpenModelChooser();
                }
                File file = fileChooser.openFile(ExperimentFrame.this);
                if (file != null) {
                    try {
                        ExperimentLoader loader = new ExperimentLoader(file);
                        LoadDialog progress = new LoadDialog(ExperimentFrame.this,
                                loader, loadExperimentTitle);

                        ExecutorService.process(progress, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                table.setRenderer(Color.RED);
                                ExperimentHistory history = loader.getExperiment();
                                table.experimentModel().setExperiment(history.getExperiment());
                                setResults(history.getDataSet());
                            }
                        }, new CallbackAction() {
                            @Override
                            public void apply() throws Exception {
                                JOptionPane.showMessageDialog(ExperimentFrame.this,
                                        progress.getErrorMessageText(),
                                        null, JOptionPane.WARNING_MESSAGE);
                            }
                        });

                    }
                    catch (Throwable e) {
                        JOptionPane.showMessageDialog(ExperimentFrame.this,
                                e.getMessage(),
                                null, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //---------------------------------------------------------------
        leftBottom.add(optionsButton,
                new GridBagConstraints(0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 10, 2), 0, 0));
        leftBottom.add(startButton,
                new GridBagConstraints(0, 1, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 10, 2), 0, 0));
        leftBottom.add(stopButton,
                new GridBagConstraints(0, 2, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 10, 2), 0, 0));
        leftBottom.add(loadButton,
                new GridBagConstraints(0, 3, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 10, 2), 0, 0));
        leftBottom.add(saveButton,
                new GridBagConstraints(0, 4, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 10, 2), 0, 0));
        //---------------------------------------------------------------
        right.setBorder(PanelBorderUtils.createTitledBorder("История эксперимента"));
        top.add(left,
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        top.add(leftBottom,
                new GridBagConstraints(0, 1, 1, 1, 0, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        top.add(right,
                new GridBagConstraints(1, 0, 1, 2, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        //----------------------------------------------
        this.add(top,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(bottom,
                new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(progress,
                new GridBagConstraints(0, 2, 1, 1, 1, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
        this.getRootPane().setDefaultButton(startButton);
    }

    /**
     *
     */
    protected class SwingWorkerConstruction extends SwingWorker<Void, Void> {

        IterativeExperiment object;
        boolean error;

        public SwingWorkerConstruction() {
            object = getExperiment().getIterativeExperiment();
            this.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        progress.setValue((Integer) evt.getNewValue());
                    }
                }
            });

        }

        @Override
        protected Void doInBackground() {
            try {
                while (!isCancelled() && object.hasNext()) {

                    try {
                        ClassifierDescriptor classifier = object.next();
                        if (!isCancelled()) {
                            table.addExperiment(classifier);
                        }
                    }
                    catch (Exception e) {}

                    if (!isCancelled()) {
                        setProgress(object.getPercent());
                    }
                }
            } catch (Throwable e) {
                error = true;
                text.setText(e.toString());
            }
            return null;
        }

        @Override
        protected void done() {
            setProgress(100);
            table.setRenderer(Color.RED);
            setStateForButtons(true);
            setStateForOptions(true);
            table.sort();
            if (!error) {
                setResults();
            }
        }

    } //End of class SwingWorkerConstruction


    /**
     *
     */
    private static class ExperimentLoader implements CallbackAction {

        ExperimentHistory experiment;
        final File file;

        public ExperimentLoader(File file) {
            this.file = file;
        }

        public ExperimentHistory getExperiment() {
            return experiment;
        }

        @Override
        public void apply() throws Exception {
            experiment = (ExperimentHistory) ModelConverter.loadModel(file);
        }
    }

}
