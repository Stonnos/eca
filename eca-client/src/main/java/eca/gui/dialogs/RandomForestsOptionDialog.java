/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.IntegerDocument;
import eca.gui.validators.TextFieldInputVerifier;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Random forests algorithm options dialog frame.
 */
public class RandomForestsOptionDialog extends BaseOptionsDialog<RandomForests> {

    private static final String OPTIONS_TITLE = "Параметры леса";

    private static final String TREES_NUM_TITLE = "Количество деревьев:";

    private static final String MIN_OBJ_TITLE = "Минимальное число объектов в листе:";

    private static final String MAX_DEPTH_TITLE = "Максимальная глубина дерева:";

    private static final String NUM_RANDOM_ATTR_TITLE = "Число случайных атрибутов:";
    private static final String RANDOM_ATTR_EXCEEDED_ERROR_FORMAT = "Число случайных атрибутов должно быть не больше %d";
    private static final String DECISION_TREE_ALGORITHM_TEXT = "Дерево решений: ";
    private static final String NUM_RANDOM_SPLITS_TEXT = "Число случайных расщеплений:";
    private static final String USE_BOOTSTRAP_SAMPLE_TEXT = "Использование бутстрэп - выборок";

    private JTextField numClassifiersText;
    private JTextField minObjText;
    private JTextField maxDepthText;
    private JTextField numRandomAttrText;
    private JComboBox<String> treeAlgorithmBox;

    private OptionsSetter optionsSetter;
    private EmptyTextFieldSearch emptyTextFieldSearch;

    public RandomForestsOptionDialog(Window parent, String title,
                                     RandomForests forest, Instances data) {
        super(parent, title, forest, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        //------------------------------------
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(OPTIONS_TITLE));
        //------------------------------------
        numClassifiersText = new JTextField(TEXT_FIELD_LENGTH);
        numClassifiersText.setDocument(new IntegerDocument(5));
        numClassifiersText.setInputVerifier(new TextFieldInputVerifier());
        minObjText = new JTextField(TEXT_FIELD_LENGTH);
        minObjText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        minObjText.setInputVerifier(new TextFieldInputVerifier());
        maxDepthText = new JTextField(TEXT_FIELD_LENGTH);
        maxDepthText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        maxDepthText.setInputVerifier(new TextFieldInputVerifier());
        numRandomAttrText = new JTextField(TEXT_FIELD_LENGTH);
        numRandomAttrText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        numRandomAttrText.setInputVerifier(new TextFieldInputVerifier());
        treeAlgorithmBox = new JComboBox<>();

        for (DecisionTreeType treeType : DecisionTreeType.values()) {
            treeAlgorithmBox.addItem(treeType.getDescription());
        }
        //-------------------------------------------------------
        optionPanel.add(new JLabel(DECISION_TREE_ALGORITHM_TEXT), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(treeAlgorithmBox, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(TREES_NUM_TITLE),
                new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numClassifiersText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(MIN_OBJ_TITLE),
                new GridBagConstraints(0, 2, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(minObjText, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(MAX_DEPTH_TITLE),
                new GridBagConstraints(0, 3, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(maxDepthText, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(NUM_RANDOM_ATTR_TITLE), new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numRandomAttrText, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        addAdditionalFormFields(optionPanel);

        //------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = false;
                setVisible(false);
            }
        });
        //-----------------------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JTextField text = emptyTextFieldSearch.findFirstEmptyField();
                if (text != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(RandomForestsOptionDialog.this, text);
                } else if (Integer.parseInt(numRandomAttrText.getText()) > data.numAttributes() - 1) {
                    JOptionPane.showMessageDialog(RandomForestsOptionDialog.this,
                            String.format(RANDOM_ATTR_EXCEEDED_ERROR_FORMAT, data.numAttributes() - 1),
                            INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                    numRandomAttrText.requestFocusInWindow();
                } else {
                    try {
                        optionsSetter.setClassifierOptions();
                        dialogResult = true;
                        setVisible(false);

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(RandomForestsOptionDialog.this,
                                e.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));

        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
        numClassifiersText.requestFocusInWindow();
    }

    @Override
    public final void showDialog() {
        optionsSetter.setFormOptions();
        super.showDialog();
    }

    private void addAdditionalFormFields(JPanel optionPanel) {
        if (classifier instanceof ExtraTreesClassifier) {

            ExtraTreesClassifier extraTreesClassifier = (ExtraTreesClassifier) classifier;

            final JTextField numRandomSplitsField = new JTextField(TEXT_FIELD_LENGTH);
            numRandomSplitsField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
            numRandomSplitsField.setInputVerifier(new TextFieldInputVerifier());

            final JCheckBox useBootstrapSamplesCheckBox = new JCheckBox(USE_BOOTSTRAP_SAMPLE_TEXT);

            optionsSetter = new OptionsSetter() {

                @Override
                void setFormOptions() {
                    super.setFormOptions();
                    numRandomSplitsField.setText(String.valueOf(extraTreesClassifier.getNumRandomSplits()));
                    useBootstrapSamplesCheckBox.setSelected(extraTreesClassifier.isUseBootstrapSamples());
                }

                @Override
                void setClassifierOptions() {
                    super.setClassifierOptions();
                    try {
                        extraTreesClassifier.setNumRandomSplits(Integer.parseInt(numRandomSplitsField.getText()));
                    } catch (Exception ex) {
                        numRandomSplitsField.requestFocusInWindow();
                        throw new RuntimeException(ex);
                    }
                    extraTreesClassifier.setUseBootstrapSamples(useBootstrapSamplesCheckBox.isSelected());
                }
            };

            emptyTextFieldSearch = new EmptyTextFieldSearch() {
                @Override
                public JTextField findFirstEmptyField() {
                    return GuiUtils.searchFirstEmptyField(numClassifiersText, minObjText,
                            maxDepthText, numRandomAttrText, numRandomSplitsField);
                }
            };

            optionPanel.add(new JLabel(NUM_RANDOM_SPLITS_TEXT), new GridBagConstraints(0, 5, 1, 1, 1, 1,
                    GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
            optionPanel.add(numRandomSplitsField, new GridBagConstraints(1, 5, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

            optionPanel.add(useBootstrapSamplesCheckBox, new GridBagConstraints(0, 6, 2, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 20, 10), 0, 0));

        } else {
            optionsSetter = new OptionsSetter();

            emptyTextFieldSearch = new EmptyTextFieldSearch() {
                @Override
                public JTextField findFirstEmptyField() {
                    return GuiUtils.searchFirstEmptyField(numClassifiersText, minObjText,
                            maxDepthText, numRandomAttrText);
                }
            };
        }
    }

    private class OptionsSetter {

        void setFormOptions() {
            numClassifiersText.setText(String.valueOf(classifier.getIterationsNum()));
            minObjText.setText(String.valueOf(classifier.getMinObj()));
            maxDepthText.setText(String.valueOf(classifier.getMaxDepth()));
            numRandomAttrText.setText(String.valueOf(classifier.getNumRandomAttr()));
            treeAlgorithmBox.setSelectedItem(classifier.getDecisionTreeType().getDescription());
        }

        void setClassifierOptions() {
            classifier.setIterationsNum(Integer.parseInt(numClassifiersText.getText()));
            classifier.setMinObj(Integer.parseInt(minObjText.getText()));
            classifier.setMaxDepth(Integer.parseInt(maxDepthText.getText()));
            classifier.setNumRandomAttr(Integer.parseInt(numRandomAttrText.getText()));
            String decisionTreeAlgorithm = treeAlgorithmBox.getSelectedItem().toString();
            classifier.setDecisionTreeType(DecisionTreeType.findByDescription(decisionTreeAlgorithm));
        }

    }

    private interface EmptyTextFieldSearch {

        JTextField findFirstEmptyField();
    }

}
