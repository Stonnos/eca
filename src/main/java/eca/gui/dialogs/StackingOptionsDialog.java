/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.ensemble.ClassifiersSet;
import eca.ensemble.StackingClassifier;
import eca.gui.BaseClassifiersListModel;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.enums.ClassifiersNames;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Рома
 */
public class StackingOptionsDialog extends BaseOptionsDialog<StackingClassifier> {

    private static final String metaSetTitle = "Формирование мета-данных";
    private static final String metaClasssifierTitle = "Выбор мета-классификатора";

    private JList<String> algorithms;
    private JList<String> selectedAlgorithms;
    private BaseClassifiersListModel model;
    private JComboBox<String> meta;
    private JButton options;

    private JRadioButton useTrainingSet;
    private JRadioButton useTestingSet;
    private final JSpinner foldsSpinner = new JSpinner();

    private BaseOptionsDialog metaCls;

    public StackingOptionsDialog(Window parent, String title,
                                 StackingClassifier forest, Instances data) {
        super(parent, title, forest, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        //-----------------------------------------
        this.makeGUI();
        this.setParam();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public void setMetaEnabled(boolean flag) {
        meta.setEnabled(flag);
        options.setEnabled(flag);
    }

    public void addClassifiers(ClassifiersSet classifiers) {
        model.clear();
        for (Classifier c : classifiers) {
            model.addClassifier(c);
        }
    }

    private void setParam() {
        useTestingSet.setSelected(classifier.getUseCrossValidation());
        ((SpinnerNumberModel) foldsSpinner.getModel()).setValue(classifier.getNumFolds());
    }

    private void makeGUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(PanelBorderUtils.createTitledBorder(metaSetTitle));
        ButtonGroup group = new ButtonGroup();
        useTrainingSet = new JRadioButton(TestingSetOptionsDialog.initialMethodTitle);
        useTestingSet = new JRadioButton(TestingSetOptionsDialog.cvMethodTitle);
        useTrainingSet.setSelected(true);
        group.add(useTrainingSet);
        group.add(useTestingSet);
        foldsSpinner.setModel(new SpinnerNumberModel(classifier.getNumFolds(), 2, 100, 1));
        foldsSpinner.setEnabled(false);
        panel.add(useTrainingSet, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        panel.add(useTestingSet, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        panel.add(new JLabel(TestingSetOptionsDialog.blocksNumTitle), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        panel.add(foldsSpinner, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        //--------------------------------------------------------------------
        this.add(panel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 0), 0, 0));
        //---------------------------------
        //--------------------------------
        useTestingSet.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                foldsSpinner.setEnabled(useTestingSet.isSelected());
            }
        });
        //---------------------------------
        //-------------------------------------------------------------
        Dimension dim = new Dimension(300, 180);
        JPanel algorithmsPanel = new JPanel(new GridBagLayout());
        String[] items = {ClassifiersNames.ID3, ClassifiersNames.C45,
                ClassifiersNames.CART, ClassifiersNames.CHAID, ClassifiersNames.NEURAL_NETWORK,
                ClassifiersNames.LOGISTIC, ClassifiersNames.KNN};
        algorithms = new JList<>(items);
        algorithms.setPreferredSize(dim);
        algorithms.setMinimumSize(dim);
        algorithms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //-------------------------------------------------
        JScrollPane algorithmsPane = new JScrollPane(algorithms);
        algorithmsPane.setPreferredSize(dim);
        algorithmsPanel.setBorder(PanelBorderUtils.createTitledBorder("Доступные классификаторы"));
        JPanel selectedPanel = new JPanel(new GridBagLayout());
        model = new BaseClassifiersListModel(data(), this);
        selectedAlgorithms = new JList<>(model);
        //  selectedAlgorithms.setPreferredSize(dim);
        //  selectedAlgorithms.setMinimumSize(dim);
        selectedAlgorithms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedPane = new JScrollPane(selectedAlgorithms);
        selectedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectedPanel.setBorder(PanelBorderUtils.createTitledBorder("Выбранные классификаторы"));
        selectedPane.setPreferredSize(dim);
        //-----------------------------------------------------------------
        final JButton addButton = new JButton("Добавить");
        addButton.setEnabled(false);
        final JButton removeButton = new JButton("Удалить");
        removeButton.setEnabled(false);
        //-------------------------------------------------------------
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                model.addElement(algorithms.getSelectedValue());
            }
        });
        //-------------------------------------------------------------
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                model.remove(selectedAlgorithms.getSelectedIndex());
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
                removeButton.setEnabled(!model.isEmpty());
            }
        });
        //-------------------------------------------------------------
        selectedAlgorithms.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int i = selectedAlgorithms.locationToIndex(e.getPoint());
                    if (model.getWindow(i) != null) {
                        model.getWindow(i).showDialog();
                    }
                }
            }

        });
        //-------------------------------------------------------------
        algorithmsPanel.add(algorithmsPane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        algorithmsPanel.add(addButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        //-------------------------------------------------------------
        selectedPanel.add(selectedPane, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 5, 0), 0, 0));
        selectedPanel.add(removeButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        //-------------------------------------------------------------
        this.add(algorithmsPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 8, 0), 0, 0));
        this.add(selectedPanel, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 8, 0), 0, 0));
        //-------------------------------------------------------------
        meta = new JComboBox<>(items);
        meta.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                try {
                    switch ((String) meta.getSelectedItem()) {
                        case ClassifiersNames.ID3:
                            metaCls = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                    ClassifiersNames.ID3, new ID3(), data);
                            break;

                        case ClassifiersNames.C45:
                            metaCls = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                    ClassifiersNames.C45, new C45(), data);
                            break;

                        case ClassifiersNames.CART:
                            metaCls = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                    ClassifiersNames.CART, new CART(), data);
                            break;

                        case ClassifiersNames.CHAID:
                            metaCls = new DecisionTreeOptionsDialog(StackingOptionsDialog.this,
                                    ClassifiersNames.CHAID, new CHAID(), data);
                            break;

                        case ClassifiersNames.NEURAL_NETWORK:
                            metaCls = new NetworkOptionsDialog(StackingOptionsDialog.this,
                                    ClassifiersNames.NEURAL_NETWORK, new NeuralNetwork(data), data);
                            break;

                        case ClassifiersNames.LOGISTIC:
                            metaCls = new LogisticOptionsDialogBase(StackingOptionsDialog.this,
                                    ClassifiersNames.LOGISTIC, new Logistic(), data);
                            break;

                        case ClassifiersNames.KNN:
                            metaCls = new KNNOptionDialog(StackingOptionsDialog.this,
                                    ClassifiersNames.KNN, new KNearestNeighbours(), data);
                            break;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StackingOptionsDialog.this,
                            e.getMessage(),
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        meta.setSelectedIndex(1);
        options = new JButton("Настройка параметров");
        options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                metaCls.showDialog();
            }
        });
        //----------------------------------------------------------------
        JPanel metaPanel = new JPanel(new GridBagLayout());
        metaPanel.setBorder(PanelBorderUtils.createTitledBorder(metaClasssifierTitle));
        metaPanel.add(meta, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        metaPanel.add(options, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 0));
        this.add(metaPanel, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 8, 0), 0, 0));
        //--------------------------------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (model.isEmpty()) {
                    JOptionPane.showMessageDialog(StackingOptionsDialog.this,
                            "Необходимо выбрать базовые классификаторы!",
                            "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                } else {
                    if (useTestingSet.isSelected()) {
                        classifier.setUseCrossValidation(true);
                        classifier.setNumFolds(((SpinnerNumberModel) foldsSpinner.getModel()).getNumber().intValue());
                    }
                    ClassifiersSet set = new ClassifiersSet();
                    for (BaseOptionsDialog frame : model.getFrames()) {
                        set.addClassifier(frame.classifier());
                    }
                    classifier.setClassifiers(set);
                    classifier.setMetaClassifier(metaCls.classifier());
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
        this.add(okButton, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

}
