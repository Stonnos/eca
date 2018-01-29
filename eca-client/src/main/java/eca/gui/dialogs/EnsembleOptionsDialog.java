/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.dictionary.ClassifiersNamesDictionary;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.sampling.SamplingMethod;
import eca.gui.BaseClassifiersListModel;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.listeners.BaseClassifiersListMouseListener;
import eca.gui.text.EstimateDocument;
import eca.gui.text.IntegerDocument;
import eca.gui.validators.TextFieldInputVerifier;
import eca.text.NumericFormat;
import eca.util.ThreadsUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class EnsembleOptionsDialog extends BaseOptionsDialog<AbstractHeterogeneousClassifier> {

    private static final int FIELD_LENGTH = 5;

    private static final String ITS_NUM_TITLE = "Число итераций:";
    private static final String MAX_ERROR_TITLE = "Макс. допустимая ошибка классификатора:";
    private static final String MIN_ERROR_TITLE = "Мин. допустимая ошибка классификатора:";

    private static final String SAMPLING_TITLE = "Формирование обучающих выборок";

    private static final String CLS_SELECTION_TITLE = "Выбор классификатора на каждой итерации";
    private static final String[] CLS_SELECTION_METHOD = {"Случайный классификатор", "Оптимальный классификатор"};

    private static final String VOTES_TITLE = "Выбор метода голосования";

    private static final String[] VOTES_METHOD = {"Метод большинства голосов", "Метод взвешенного голосования"};
    private static final String MAIN_OPTIONS_TITLE = "Основные параметры";
    private static final String AVAILABLE_CLASSIFIERS_TITLE = "Доступные классификаторы";
    private static final String SELECTED_CLASSIFIERS_TITLE = "Выбранные классификаторы";
    private static final String ADD_BUTTON_TEXT = "Добавить";
    private static final String DELETE_BUTTON_TEXT = "Удалить";
    private static final String MAIN_OPTIONS_TAB_TITLE = "Основные настройки";
    private static final String ADDITIONAL_OPTIONS_TAB_TITLE = "Дополнительные настройки";
    private static final String NUM_THREADS_TITLE = "Число потоков:";
    private static final String EMPTY_CLASSIFIERS_SET_ERROR_MESSAGE =
            "Необходимо выбрать индивидуальные классификаторы!";

    private static final String[] AVAILABLE_INDIVIDUAL_CLASSIFIERS = new String[] {
            ClassifiersNamesDictionary.ID3, ClassifiersNamesDictionary.C45,
            ClassifiersNamesDictionary.CART, ClassifiersNamesDictionary.CHAID,
            ClassifiersNamesDictionary.NEURAL_NETWORK,
            ClassifiersNamesDictionary.LOGISTIC, ClassifiersNamesDictionary.KNN,
            ClassifiersNamesDictionary.J48
    };

    private static final int ALGORITHMS_LIST_WIDTH = 320;
    private static final int ALGORITHMS_HEIGHT_HEIGHT = 265;
    private static final Dimension TAB_DIMENSION = new Dimension(620, 430);

    private final DecimalFormat estimateFormat = NumericFormat.getInstance();

    private JTabbedPane pane;
    private JPanel firstPanel;
    private JPanel secondPanel;
    private JTextField numClassifiersTextField;
    private JTextField classifierMinErrorTextField;
    private JTextField classifierMaxErrorTextField;
    private JList<String> algorithms;
    private JList<String> selectedAlgorithms;
    private BaseClassifiersListModel baseClassifiersListModel;
    private JPanel samplePanel;
    private JRadioButton initialRadioButton;
    private JRadioButton baggingRadioButton;
    private JRadioButton randomRadioButton;
    private JRadioButton randomBaggingRadioButton;

    private JRadioButton majorityRadioButton;
    private JRadioButton weightedRadioButton;

    private JRadioButton randomClsRadioButton;
    private JRadioButton optimalClsRadioButton;

    private JSpinner threadsSpinner;

    public EnsembleOptionsDialog(JFrame parent, String title,
                                 AbstractHeterogeneousClassifier classifier, Instances data, final int digits) {
        super(parent, title, classifier, data);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.createFormat();
        this.createGUI(digits);
        this.pack();
        this.setLocationRelativeTo(parent);
        numClassifiersTextField.requestFocusInWindow();
    }

    @Override
    public void showDialog() {
        this.setOptions();
        threadsSpinner.setEnabled(classifier instanceof HeterogeneousClassifier);
        super.showDialog();
    }

    public void setSampleEnabled(boolean flag) {
        pane.setEnabledAt(1, flag);
    }

    public void addClassifiers(ClassifiersSet classifiers) {
        baseClassifiersListModel.clear();
        for (Classifier c : classifiers) {
            baseClassifiersListModel.addClassifier(c);
        }
    }

    private void createGUI(final int digits) {
        pane = new JTabbedPane();
        firstPanel = new JPanel(new GridBagLayout());
        firstPanel.setPreferredSize(TAB_DIMENSION);
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(MAIN_OPTIONS_TITLE));
        numClassifiersTextField = new JTextField(TEXT_FIELD_LENGTH);
        numClassifiersTextField.setDocument(new IntegerDocument(FIELD_LENGTH));
        numClassifiersTextField.setInputVerifier(new TextFieldInputVerifier());
        classifierMinErrorTextField = new JTextField(TEXT_FIELD_LENGTH);
        classifierMinErrorTextField.setDocument(new EstimateDocument(FIELD_LENGTH));
        classifierMinErrorTextField.setInputVerifier(new TextFieldInputVerifier());
        classifierMaxErrorTextField = new JTextField(TEXT_FIELD_LENGTH);
        classifierMaxErrorTextField.setDocument(new EstimateDocument(FIELD_LENGTH));
        classifierMaxErrorTextField.setInputVerifier(new TextFieldInputVerifier());
        threadsSpinner = new JSpinner();
        //----------------------------------------------------
        firstPanel.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
        optionPanel.add(new JLabel(ITS_NUM_TITLE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numClassifiersTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(MIN_ERROR_TITLE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMinErrorTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(MAX_ERROR_TITLE), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMaxErrorTextField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(NUM_THREADS_TITLE), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(threadsSpinner, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        Dimension dim = new Dimension(ALGORITHMS_LIST_WIDTH, ALGORITHMS_HEIGHT_HEIGHT);
        JPanel algorithmsPanel = new JPanel(new GridBagLayout());
        algorithms = new JList<>(AVAILABLE_INDIVIDUAL_CLASSIFIERS);
        algorithms.setPreferredSize(dim);
        algorithms.setMinimumSize(dim);
        algorithms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //-------------------------------------------------
        JScrollPane algorithmsPane = new JScrollPane(algorithms);
        algorithmsPane.setPreferredSize(dim);
        algorithmsPanel.setBorder(PanelBorderUtils.createTitledBorder(AVAILABLE_CLASSIFIERS_TITLE));
        JPanel selectedPanel = new JPanel(new GridBagLayout());
        baseClassifiersListModel = new BaseClassifiersListModel(data(), this, digits);
        selectedAlgorithms = new JList<>(baseClassifiersListModel);
        selectedAlgorithms.setMinimumSize(dim);
        selectedAlgorithms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedPane = new JScrollPane(selectedAlgorithms);
        selectedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectedPanel.setBorder(PanelBorderUtils.createTitledBorder(SELECTED_CLASSIFIERS_TITLE));
        selectedPane.setPreferredSize(dim);

        final JButton addButton = new JButton(ADD_BUTTON_TEXT);
        addButton.setEnabled(false);
        final JButton removeButton = new JButton(DELETE_BUTTON_TEXT);
        removeButton.setEnabled(false);
        //-------------------------------------------------------------
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                baseClassifiersListModel.addElement(algorithms.getSelectedValue());
            }
        });
        //-------------------------------------------------------------
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                baseClassifiersListModel.remove(selectedAlgorithms.getSelectedIndex());
                removeButton.setEnabled(false);
            }
        });
        //-------------------------------------------------------------
        algorithms.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                addButton.setEnabled(true);
            }
        });
        selectedAlgorithms.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                removeButton.setEnabled(!baseClassifiersListModel.isEmpty());
            }
        });
        //-------------------------------------------------------------
        selectedAlgorithms.addMouseListener(new BaseClassifiersListMouseListener(selectedAlgorithms,
                baseClassifiersListModel));
        //-------------------------------------------------------------
        algorithmsPanel.add(algorithmsPane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
        algorithmsPanel.add(addButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        //-------------------------------------------------------------
        selectedPanel.add(selectedPane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
        selectedPanel.add(removeButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        //-------------------------------------------------------------
        firstPanel.add(algorithmsPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 8, 0), 0, 0));
        firstPanel.add(selectedPanel, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 8, 0), 0, 0));
        //--------------------------------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();
        //--------------------------------------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JTextField text = GuiUtils.searchFirstEmptyField(numClassifiersTextField,
                        classifierMinErrorTextField, classifierMaxErrorTextField);
                if (text != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(EnsembleOptionsDialog.this, text);
                } else if (isValidate()) {
                    createClassifiersSet();
                    dialogResult = true;
                    setVisible(false);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = false;
                setVisible(false);
            }
        });
        //--------------------------------------------------------------
        pane.add(firstPanel, MAIN_OPTIONS_TAB_TITLE);
        //--------------------------------------------------------------
        secondPanel = new JPanel(new GridBagLayout());
        secondPanel.setPreferredSize(dim);
        samplePanel = new JPanel(new GridBagLayout());
        samplePanel.setBorder(PanelBorderUtils.createTitledBorder(SAMPLING_TITLE));
        ButtonGroup group = new ButtonGroup();
        initialRadioButton = new JRadioButton(SamplingMethod.INITIAL.getDescription());
        baggingRadioButton = new JRadioButton(SamplingMethod.BAGGING.getDescription());
        randomRadioButton = new JRadioButton(SamplingMethod.RANDOM.getDescription());
        randomBaggingRadioButton = new JRadioButton(SamplingMethod.RANDOM_BAGGING.getDescription());
        group.add(initialRadioButton);
        group.add(baggingRadioButton);
        group.add(randomRadioButton);
        group.add(randomBaggingRadioButton);
        //--------------------------------
        initialRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setSamplingMethod(SamplingMethod.INITIAL);
                }
            }
        });
        baggingRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setSamplingMethod(SamplingMethod.BAGGING);
                }
            }
        });
        randomRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setSamplingMethod(SamplingMethod.RANDOM);
                }
            }
        });
        randomBaggingRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setSamplingMethod(SamplingMethod.RANDOM_BAGGING);
                }
            }
        });
        initialRadioButton.setSelected(true);
        //-----------------------------------------------
        samplePanel.add(initialRadioButton, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        samplePanel.add(baggingRadioButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        samplePanel.add(randomRadioButton, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        samplePanel.add(randomBaggingRadioButton, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        secondPanel.add(samplePanel, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        //------------------------------------------------------
        JPanel clsMethodPanel = new JPanel(new GridBagLayout());
        clsMethodPanel.setBorder(PanelBorderUtils.createTitledBorder(CLS_SELECTION_TITLE));
        ButtonGroup clsGroup = new ButtonGroup();
        randomClsRadioButton = new JRadioButton(CLS_SELECTION_METHOD[0]);
        optimalClsRadioButton = new JRadioButton(CLS_SELECTION_METHOD[1]);
        clsGroup.add(randomClsRadioButton);
        clsGroup.add(optimalClsRadioButton);
        //--------------------------------
        randomClsRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseRandomClassifier(true);
                }
            }
        });
        optimalClsRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseRandomClassifier(false);
                }
            }
        });
        randomClsRadioButton.setSelected(true);
        clsMethodPanel.add(randomClsRadioButton, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        clsMethodPanel.add(optimalClsRadioButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        secondPanel.add(clsMethodPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        //------------------------------------------------------------
        JPanel votesPanel = new JPanel(new GridBagLayout());
        votesPanel.setBorder(PanelBorderUtils.createTitledBorder(VOTES_TITLE));
        ButtonGroup votesGroup = new ButtonGroup();
        majorityRadioButton = new JRadioButton(VOTES_METHOD[0]);
        weightedRadioButton = new JRadioButton(VOTES_METHOD[1]);
        votesGroup.add(majorityRadioButton);
        votesGroup.add(weightedRadioButton);
        //--------------------------------
        majorityRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseWeightedVotesMethod(false);
                }
            }
        });
        weightedRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseWeightedVotesMethod(true);
                }
            }
        });
        majorityRadioButton.setSelected(true);
        votesPanel.add(majorityRadioButton, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        votesPanel.add(weightedRadioButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        secondPanel.add(votesPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        //------------------------------------------------------
        pane.add(secondPanel, ADDITIONAL_OPTIONS_TAB_TITLE);
        //------------------------------------------------------
        this.add(pane, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

    private void createFormat() {
        estimateFormat.setMaximumIntegerDigits(0);
        estimateFormat.setMaximumFractionDigits(FIELD_LENGTH);
    }

    private void setOptions() {
        numClassifiersTextField.setText(String.valueOf(classifier.getIterationsNum()));
        classifierMaxErrorTextField.setText(estimateFormat.format(classifier.getMaxError()));
        classifierMinErrorTextField.setText(estimateFormat.format(classifier.getMinError()));
        threadsSpinner.setModel(
                new SpinnerNumberModel(EnsembleUtils.getNumThreads(classifier), ThreadsUtils.MIN_NUM_THREADS,
                        ThreadsUtils.getMaxNumThreads(), 1));
    }

    private boolean isValidate() {
        JTextField textField = classifierMinErrorTextField;
        try {
            textField = numClassifiersTextField;
            classifier.setIterationsNum(Integer.parseInt(numClassifiersTextField.getText().trim()));
            textField = classifierMinErrorTextField;
            classifier.setMinError(estimateFormat
                    .parse(classifierMinErrorTextField.getText().trim()).doubleValue());
            textField = classifierMaxErrorTextField;
            classifier.setMaxError(estimateFormat
                    .parse(classifierMaxErrorTextField.getText().trim()).doubleValue());
            classifier.setNumThreads(((SpinnerNumberModel) threadsSpinner.getModel()).getNumber().intValue());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(EnsembleOptionsDialog.this,
                    e.getMessage(),
                    INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
            textField.requestFocusInWindow();
            return false;
        }
        if (baseClassifiersListModel.isEmpty()) {
            JOptionPane.showMessageDialog(EnsembleOptionsDialog.this,
                    EMPTY_CLASSIFIERS_SET_ERROR_MESSAGE,
                    INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void createClassifiersSet() {
        ClassifiersSet classifiersSet = new ClassifiersSet();
        for (BaseOptionsDialog frame : baseClassifiersListModel.getFrames()) {
            classifiersSet.addClassifier(frame.classifier());
        }
        classifier.setClassifiersSet(classifiersSet);
    }
}
