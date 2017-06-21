/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import eca.gui.BaseClassifiersListModel;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import java.text.DecimalFormat;
import eca.gui.text.IntegerDocument;
import eca.gui.text.EstimateDocument;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.gui.enums.ClassifiersNames;
import eca.ensemble.Sampler;
import weka.core.Instances;
import eca.gui.text.NumericFormat;
import weka.classifiers.Classifier;

/**
 *
 * @author Рома
 */
public class EnsembleOptionsDialog extends BaseOptionsDialog<AbstractHeterogeneousClassifier> {

    private static final int FIELD_LENGTH = 5;

    private static final String itsNumTitle = "Число итераций:";
    private static final String maxErrorTitle = "Макс. допустимая ошибка классификатора:";
    private static final String minErrorTitle = "Мин. допустимая ошибка классификатора:";

    private static final String samplingTitle = "Формирование обучающих выборок";

    private static final String[] samplingMethod = {"Использование исходной выборки",
                                                    "Бутстрэп выборки",
                                                    "Случайные подвыборки",
                                                    "Бутстрэп выборки случайного размера"};

    private static final String clsSelectionTitle = "Выбор классификатора на каждой итерации";
    private static final String[] clsSelectionMethod = {"Случайный классификатор", "Оптимальный классификатор"};

    private static final String votesTitle = "Выбор метода голосования";

    private static final String[] votesMethod = {"Метод большинства голосов", "Метод взвешенного голосования"};


    private JTabbedPane pane;
    private JPanel firstPanel;
    private JPanel secondPanel;
    private JTextField numClassifiersText;
    private JTextField classifierMinErrorText;
    private JTextField classifierMaxErrorText;
    private JList<String> algorithms;
    private JList<String> selectedAlgorithms;
    private BaseClassifiersListModel model;
    private JPanel samplePanel;
    private JRadioButton initial;
    private JRadioButton bagging;
    private JRadioButton random;
    private JRadioButton randomBagging;
    private final DecimalFormat estimateFormat = NumericFormat.getInstance();

    private JRadioButton majority;
    private JRadioButton weighted;

    private JRadioButton randomCls;
    private JRadioButton optimalCls;

    public EnsembleOptionsDialog(JFrame parent, String title,
            AbstractHeterogeneousClassifier classifier, Instances data) {
        super(parent, title, classifier, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.createFormat();
        this.makeGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        numClassifiersText.requestFocusInWindow();
    }

    @Override
    public void showDialog() {
        this.setOptions();
        super.showDialog();
    }

    public void setSampleEnabled(boolean flag) {
        pane.setEnabledAt(1, flag);
    }

    public void addClassifiers(ClassifiersSet classifiers) {
        model.clear();
        for (Classifier c : classifiers) {
            model.addClassifier(c);
        }
    }

    private void makeGUI() {
        pane = new JTabbedPane();
        Dimension dim1 = new Dimension(620, 375);

        firstPanel = new JPanel(new GridBagLayout());
        firstPanel.setPreferredSize(dim1);
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder("Основные параметры"));
        numClassifiersText = new JTextField(TEXT_FIELD_LENGTH);
        numClassifiersText.setDocument(new IntegerDocument(FIELD_LENGTH));
        classifierMinErrorText = new JTextField(TEXT_FIELD_LENGTH);
        classifierMinErrorText.setDocument(new EstimateDocument(FIELD_LENGTH));
        classifierMaxErrorText = new JTextField(TEXT_FIELD_LENGTH);
        classifierMaxErrorText.setDocument(new EstimateDocument(FIELD_LENGTH));
        //----------------------------------------------------
        firstPanel.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
        optionPanel.add(new JLabel(itsNumTitle),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numClassifiersText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(minErrorTitle), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMinErrorText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(maxErrorTitle), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMaxErrorText, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //-------------------------------------------------------------
        //-------------------------------------------------------------
        Dimension dim = new Dimension(320, 265);
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
        selectedAlgorithms.setMinimumSize(dim);
        selectedAlgorithms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedPane = new JScrollPane(selectedAlgorithms);
        selectedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectedPanel.setBorder(PanelBorderUtils.createTitledBorder("Выбранные классификаторы"));
        selectedPane.setPreferredSize(dim);
        //-------------------------------------------------------------        
        //------------------------------------------------------------
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
                JTextField text = GuiUtils.searchFirstEmptyField(numClassifiersText,
                        classifierMinErrorText, classifierMaxErrorText);
                if (text != null) {
                    JOptionPane.showMessageDialog(EnsembleOptionsDialog.this,
                            "Заполните все поля!",
                            "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                    text.requestFocusInWindow();
                } else if (isValidate()) {
                    createEnsemble();
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
        pane.add(firstPanel, "Основные настройки");
        //--------------------------------------------------------------
        secondPanel = new JPanel(new GridBagLayout());
        secondPanel.setPreferredSize(dim);
        samplePanel = new JPanel(new GridBagLayout());
        samplePanel.setBorder(PanelBorderUtils.createTitledBorder(samplingTitle));
        ButtonGroup group = new ButtonGroup();
        initial = new JRadioButton(samplingMethod[0]);
        bagging = new JRadioButton(samplingMethod[1]);
        random = new JRadioButton(samplingMethod[2]);
        randomBagging = new JRadioButton(samplingMethod[3]);
        group.add(initial);
        group.add(bagging);
        group.add(random);
        group.add(randomBagging);
        //--------------------------------
        initial.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).sampler().setSampling(Sampler.INITIAL);
                }
            }
        });
        bagging.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).sampler().setSampling(Sampler.BAGGING);
                }
            }
        });
        random.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).sampler().setSampling(Sampler.RANDOM);
                }
            }
        });
        randomBagging.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).sampler().setSampling(Sampler.RANDOM_BAGGING);
                }
            }
        });
        initial.setSelected(true);
        //-----------------------------------------------
        samplePanel.add(initial, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        samplePanel.add(bagging, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        samplePanel.add(random, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        samplePanel.add(randomBagging, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        secondPanel.add(samplePanel, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        //------------------------------------------------------
        JPanel clsMethodPanel = new JPanel(new GridBagLayout());
        clsMethodPanel.setBorder(PanelBorderUtils.createTitledBorder(clsSelectionTitle));
        ButtonGroup clsGroup = new ButtonGroup();
        randomCls = new JRadioButton(clsSelectionMethod[0]);
        optimalCls = new JRadioButton(clsSelectionMethod[1]);
        clsGroup.add(randomCls);
        clsGroup.add(optimalCls);
        //--------------------------------
        randomCls.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseRandomClassifier(true);
                }
            }
        });
        optimalCls.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseRandomClassifier(false);
                }
            }
        });
        randomCls.setSelected(true);
        clsMethodPanel.add(randomCls, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        clsMethodPanel.add(optimalCls, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        secondPanel.add(clsMethodPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        //------------------------------------------------------------
        JPanel votesPanel = new JPanel(new GridBagLayout());
        votesPanel.setBorder(PanelBorderUtils.createTitledBorder(votesTitle));
        ButtonGroup votesGroup = new ButtonGroup();
        majority = new JRadioButton(votesMethod[0]);
        weighted = new JRadioButton(votesMethod[1]);
        votesGroup.add(majority);
        votesGroup.add(weighted);
        //--------------------------------
        majority.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseWeightedVotesMethod(false);
                }
            }
        });
        weighted.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                if (classifier instanceof HeterogeneousClassifier) {
                    ((HeterogeneousClassifier) classifier).setUseWeightedVotesMethod(true);
                }
            }
        });
        majority.setSelected(true);
        votesPanel.add(majority, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        votesPanel.add(weighted, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        secondPanel.add(votesPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        //------------------------------------------------------
        pane.add(secondPanel, "Дополнительные настройки");
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
        numClassifiersText.setText(String.valueOf(classifier.getIterationsNum()));
        classifierMaxErrorText.setText(estimateFormat.format(classifier.getMaxError()));
        classifierMinErrorText.setText(estimateFormat.format(classifier.getMinError()));
    }

    private boolean isValidate() {
        JTextField focus = classifierMinErrorText;
        try {
            classifier.setMinError(estimateFormat
                    .parse(classifierMinErrorText.getText()).doubleValue());
            focus = classifierMaxErrorText;
            classifier.setMaxError(estimateFormat
                    .parse(classifierMaxErrorText.getText()).doubleValue());
            focus = numClassifiersText;
            classifier.setIterationsNum(Integer.parseInt(numClassifiersText.getText()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(EnsembleOptionsDialog.this,
                    e.getMessage(),
                    "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
            focus.requestFocusInWindow();
            return false;
        }
        //-----------------------------
        if (model.isEmpty()) {
            JOptionPane.showMessageDialog(EnsembleOptionsDialog.this,
                    "Необходимо выбрать индивидуальные классификаторы!",
                    "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        //----------------------------------
        return true;
    }

    private void createEnsemble() {
        ClassifiersSet set = new ClassifiersSet();
        for (BaseOptionsDialog frame : model.getFrames()) {
            set.addClassifier(frame.classifier());
        }
        classifier.setClassifiersSet(set);
    }
}
