/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.core.evaluation.EvaluationMethod;
import eca.dictionary.ClassifiersNamesDictionary;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.StackingClassifier;
import eca.gui.BaseClassifiersListModel;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.listeners.BaseClassifiersListMouseListener;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import eca.trees.J48;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */
public class StackingOptionsDialog extends BaseOptionsDialog<StackingClassifier> {

    private static final String META_SET_TITLE = "Формирование мета-данных";
    private static final String META_CLASSIFIER_TITLE = "Выбор мета-классификатора";
    private static final String AVAILABLE_CLASSIFIERS_TEXT = "Доступные классификаторы";
    private static final String SELECTED_CLASSIFIERS_TEXT = "Выбранные классификаторы";
    private static final String ADD_CLASSIFIER_BUTTON_TEXT = "Добавить";
    private static final String DELETE_CLASSIFIER_BUTTON_TEXT = "Удалить";
    private static final String META_CLASSIFIER_OPTIONS_BUTTON_TEXT = "Настройка параметров";
    private static final String EMPTY_CLASSIFIERS_SET_ERROR_MESSAGE = "Необходимо выбрать базовые классификаторы!";

    private static final String[] AVAILABLE_INDIVIDUAL_CLASSIFIERS = new String[] {ClassifiersNamesDictionary.ID3,
            ClassifiersNamesDictionary.C45,
            ClassifiersNamesDictionary.CART, ClassifiersNamesDictionary.CHAID,
            ClassifiersNamesDictionary.NEURAL_NETWORK,
            ClassifiersNamesDictionary.LOGISTIC, ClassifiersNamesDictionary.KNN,
            ClassifiersNamesDictionary.J48};

    private static final Dimension ALGORITHMS_LIST_DIM = new Dimension(300, 180);

    private JList<String> algorithmsList;
    private JList<String> selectedAlgorithmsList;
    private BaseClassifiersListModel baseClassifiersListModel;
    private JComboBox<String> metaClassifierBox;
    private JButton metaOptionsButton;

    private JRadioButton useTrainingSet;
    private JRadioButton useTestingSet;
    private final JSpinner foldsSpinner = new JSpinner();

    private BaseOptionsDialog metaClsOptionsDialog;

    public StackingOptionsDialog(Window parent, String title,
                                 StackingClassifier stackingClassifier, Instances data, final int digits) {
        super(parent, title, stackingClassifier, data);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.createGUI(digits);
        this.setMetaDataSelectionMethod();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public void setMetaClassifierSelectionEnabled(boolean flag) {
        metaClassifierBox.setEnabled(flag);
        metaOptionsButton.setEnabled(flag);
    }

    public void addClassifiers(ClassifiersSet classifiers) {
        baseClassifiersListModel.clear();
        for (Classifier c : classifiers) {
            baseClassifiersListModel.addClassifier(c);
        }
    }

    private void setMetaDataSelectionMethod() {
        useTestingSet.setSelected(classifier.getUseCrossValidation());
        foldsSpinner.getModel().setValue(classifier.getNumFolds());
    }

    private void createGUI(final int digits) {
        this.add(createMetaDataSetSelectionPanel(), new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 0, 0));
        useTestingSet.addItemListener(e -> foldsSpinner.setEnabled(useTestingSet.isSelected()));
        //-------------------------------------------------------------
        this.add(createAlgorithmsSelectionPanel(), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 8, 0), 0, 0));
        this.add(createSelectedAlgorithmsPanel(digits), new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 8, 0), 0, 0));
        //-------------------------------------------------------------
        createMetaClassifierComboBox(digits);
        createMetaClassifierOptionsButton();
        //----------------------------------------------------------------
        JPanel metaPanel = new JPanel(new GridBagLayout());
        metaPanel.setBorder(PanelBorderUtils.createTitledBorder(META_CLASSIFIER_TITLE));
        metaPanel.add(metaClassifierBox, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        metaPanel.add(metaOptionsButton, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 0));
        this.add(metaPanel, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 8, 0), 0, 0));
        //--------------------------------------------------------------
        JButton okButton = createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });
        //--------------------------------------------------------------
        this.add(okButton, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

    private JButton createOkButton() {
        JButton okButton = ButtonUtils.createOkButton();
        okButton.addActionListener(e -> {
            if (baseClassifiersListModel.isEmpty()) {
                JOptionPane.showMessageDialog(StackingOptionsDialog.this,
                        EMPTY_CLASSIFIERS_SET_ERROR_MESSAGE,
                        INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
            } else {
                if (useTestingSet.isSelected()) {
                    classifier.setUseCrossValidation(true);
                    classifier.setNumFolds(((SpinnerNumberModel) foldsSpinner.getModel()).getNumber().intValue());
                }
                ClassifiersSet set = new ClassifiersSet();
                for (BaseOptionsDialog frame : baseClassifiersListModel.getFrames()) {
                    set.addClassifier(frame.classifier());
                }
                classifier.setClassifiers(set);
                classifier.setMetaClassifier(metaClsOptionsDialog.classifier());
                dialogResult = true;
                setVisible(false);
            }
        });
        return okButton;
    }

    private void createMetaClassifierOptionsButton() {
        metaOptionsButton = new JButton(META_CLASSIFIER_OPTIONS_BUTTON_TEXT);
        metaOptionsButton.addActionListener(e -> metaClsOptionsDialog.showDialog());
    }

    private JPanel createAlgorithmsSelectionPanel() {
        JPanel algorithmsPanel = new JPanel(new GridBagLayout());
        algorithmsList = new JList<>(AVAILABLE_INDIVIDUAL_CLASSIFIERS);
        algorithmsList.setPreferredSize(ALGORITHMS_LIST_DIM);
        algorithmsList.setMinimumSize(ALGORITHMS_LIST_DIM);
        algorithmsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //-------------------------------------------------
        JScrollPane algorithmsPane = new JScrollPane(algorithmsList);
        algorithmsPane.setPreferredSize(ALGORITHMS_LIST_DIM);
        algorithmsPanel.setBorder(PanelBorderUtils.createTitledBorder(AVAILABLE_CLASSIFIERS_TEXT));
        final JButton addButton = new JButton(ADD_CLASSIFIER_BUTTON_TEXT);
        addButton.setEnabled(false);
        addButton.addActionListener(e -> baseClassifiersListModel.addElement(algorithmsList.getSelectedValue()));
        algorithmsList.addListSelectionListener(e -> addButton.setEnabled(true));
        algorithmsPanel.add(algorithmsPane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        algorithmsPanel.add(addButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        return algorithmsPanel;
    }

    private JPanel createSelectedAlgorithmsPanel(int digits) {
        JPanel selectedAlgorithmsPanel = new JPanel(new GridBagLayout());
        baseClassifiersListModel = new BaseClassifiersListModel(data(), this, digits);
        selectedAlgorithmsList = new JList<>(baseClassifiersListModel);
        selectedAlgorithmsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedPane = new JScrollPane(selectedAlgorithmsList);
        selectedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectedAlgorithmsPanel.setBorder(PanelBorderUtils.createTitledBorder(SELECTED_CLASSIFIERS_TEXT));
        selectedPane.setPreferredSize(ALGORITHMS_LIST_DIM);
        //-----------------------------------------------------------------
        final JButton removeButton = new JButton(DELETE_CLASSIFIER_BUTTON_TEXT);
        removeButton.setEnabled(false);
        removeButton.addActionListener(e -> {
            baseClassifiersListModel.remove(selectedAlgorithmsList.getSelectedIndex());
            removeButton.setEnabled(false);
        });
        selectedAlgorithmsList.addListSelectionListener(
                e -> removeButton.setEnabled(!baseClassifiersListModel.isEmpty()));
        selectedAlgorithmsList.addMouseListener(new BaseClassifiersListMouseListener(selectedAlgorithmsList,
                baseClassifiersListModel));
        selectedAlgorithmsPanel.add(selectedPane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        selectedAlgorithmsPanel.add(removeButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        return selectedAlgorithmsPanel;
    }

    private JPanel createMetaDataSetSelectionPanel() {
        JPanel metaDataSetSelectionMethodPanel = new JPanel(new GridBagLayout());
        metaDataSetSelectionMethodPanel.setBorder(PanelBorderUtils.createTitledBorder(META_SET_TITLE));
        ButtonGroup group = new ButtonGroup();
        useTrainingSet = new JRadioButton(EvaluationMethod.TRAINING_DATA.getDescription());
        useTestingSet = new JRadioButton(EvaluationMethod.CROSS_VALIDATION.getDescription());
        useTrainingSet.setSelected(true);
        group.add(useTrainingSet);
        group.add(useTestingSet);
        foldsSpinner.setModel(
                new SpinnerNumberModel(classifier.getNumFolds(), CommonDictionary.MINIMUM_NUMBER_OF_FOLDS,
                        CommonDictionary.MAXIMUM_NUMBER_OF_FOLDS, 1));
        foldsSpinner.setEnabled(false);
        metaDataSetSelectionMethodPanel.add(useTrainingSet, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        metaDataSetSelectionMethodPanel.add(useTestingSet, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        metaDataSetSelectionMethodPanel.add(new JLabel(EvaluationMethodOptionsDialog.BLOCKS_NUM_TITLE),
                new GridBagConstraints(0, 2, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        metaDataSetSelectionMethodPanel.add(foldsSpinner, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        return metaDataSetSelectionMethodPanel;
    }

    private void createMetaClassifierComboBox(int digits) {
        metaClassifierBox = new JComboBox<>(AVAILABLE_INDIVIDUAL_CLASSIFIERS);
        metaClassifierBox.addItemListener(event -> {
            try {
                String selectedClassifier = (String) metaClassifierBox.getSelectedItem();
                if (StringUtils.isEmpty(selectedClassifier)) {
                    throw new IllegalArgumentException(EMPTY_CLASSIFIERS_SET_ERROR_MESSAGE);
                }
                switch (selectedClassifier) {
                    case ClassifiersNamesDictionary.ID3:
                        metaClsOptionsDialog = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.ID3, new ID3(), data);
                        break;

                    case ClassifiersNamesDictionary.C45:
                        metaClsOptionsDialog = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.C45, new C45(), data);
                        break;

                    case ClassifiersNamesDictionary.CART:
                        metaClsOptionsDialog = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.CART, new CART(), data);
                        break;

                    case ClassifiersNamesDictionary.CHAID:
                        metaClsOptionsDialog = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.CHAID, new CHAID(), data);
                        break;

                    case ClassifiersNamesDictionary.NEURAL_NETWORK:
                        NeuralNetwork neuralNetwork = new NeuralNetwork(data);
                        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(digits);
                        metaClsOptionsDialog = new NetworkOptionsDialog(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.NEURAL_NETWORK, neuralNetwork, data);
                        break;

                    case ClassifiersNamesDictionary.LOGISTIC:
                        metaClsOptionsDialog = new LogisticOptionsDialogBase(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.LOGISTIC, new Logistic(), data);
                        break;

                    case ClassifiersNamesDictionary.KNN:
                        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
                        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(digits);
                        metaClsOptionsDialog = new KNNOptionDialog(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.KNN, kNearestNeighbours, data);
                        break;
                    case ClassifiersNamesDictionary.J48:
                        metaClsOptionsDialog = new J48OptionsDialog(StackingOptionsDialog.this,
                                ClassifiersNamesDictionary.J48, new J48(), data);
                        break;

                    default:
                        throw new IllegalArgumentException(
                                String.format("Unexpected classifier: %s", selectedClassifier));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(StackingOptionsDialog.this,
                        e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
            }
        });
        metaClassifierBox.setSelectedIndex(1);
    }

}
