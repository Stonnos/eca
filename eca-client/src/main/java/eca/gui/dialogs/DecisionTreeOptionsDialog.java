/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.IntegerDocument;
import eca.gui.validators.TextFieldInputVerifier;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Roman Batygin
 */
public class DecisionTreeOptionsDialog extends BaseOptionsDialog<DecisionTreeClassifier> {

    private static final String TREE_OPTIONS_MESSAGE = "Параметры дерева";
    private static final String RANDOM_TREE_MESSAGE = "Случайное дерево";
    private static final String MIN_OBJ_MESSAGE = "Минимальное число объектов в листе:";
    private static final String MAX_DEPTH_MESSAGE = "Максимальная глубина дерева:";
    private static final String NUM_RANDOM_ATTR_MESSAGE = "Число случайных атрибутов:";
    private static final String BINARY_TREE_TYPE_TEXT = "Бинарное дерево";
    private static final String HI_SQUARE_TEXT =
            "<html><body>Уровень значимости для<br>статистики хи-квадрат:</body></html>";

    private static final String[] AlPHA = {"0.995", "0.99", "0.975", "0.95", "0.75", "0.5", "0.25",
            "0.1", "0.05", "0.025", "0.01", "0.005"};
    private static final String RANDOM_ATTRS_EXCEEDED_ERROR_MESSAGE =
            "Число случайных атрибутов должно быть не больше %d";
    private static final String RANDOM_SPLITS_TEXT = "Случайные расщепления атрибута";
    private static final String NUM_RANDOM_SPLITS_TEXT = "Число случайных расщеплений:";

    private OptionsSetter optionsSetter;

    private JTextField minObjTextField;
    private JTextField maxDepthTextField;
    private JCheckBox randomTreeBox;
    private JTextField numRandomAttrTextField;
    private JCheckBox binaryTreeBox;
    private JCheckBox randomSplitsBox;
    private JTextField numRandomSplitsTextField;

    public DecisionTreeOptionsDialog(Window parent, String title,
                                     DecisionTreeClassifier tree, Instances data) {
        super(parent, title, tree, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);

        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(TREE_OPTIONS_MESSAGE));
        minObjTextField = new JTextField(TEXT_FIELD_LENGTH);
        minObjTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        minObjTextField.setInputVerifier(new TextFieldInputVerifier());
        maxDepthTextField = new JTextField(TEXT_FIELD_LENGTH);
        maxDepthTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        maxDepthTextField.setInputVerifier(new TextFieldInputVerifier());

        randomTreeBox = new JCheckBox(RANDOM_TREE_MESSAGE);

        numRandomAttrTextField = new JTextField(TEXT_FIELD_LENGTH);
        numRandomAttrTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));

        randomTreeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                numRandomAttrTextField.setEditable(randomTreeBox.isSelected());
            }
        });

        binaryTreeBox = new JCheckBox(BINARY_TREE_TYPE_TEXT);

        randomSplitsBox = new JCheckBox(RANDOM_SPLITS_TEXT);

        numRandomSplitsTextField = new JTextField(TEXT_FIELD_LENGTH);
        numRandomSplitsTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));

        randomSplitsBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                numRandomSplitsTextField.setEditable(randomSplitsBox.isSelected());
            }
        });

        if (classifier instanceof CART) {
            binaryTreeBox.setEnabled(false);
        }

        optionPanel.add(new JLabel(MIN_OBJ_MESSAGE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(minObjTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(MAX_DEPTH_MESSAGE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(maxDepthTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(randomTreeBox, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(new JLabel(NUM_RANDOM_ATTR_MESSAGE), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numRandomAttrTextField, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(binaryTreeBox, new GridBagConstraints(0, 4, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(randomSplitsBox, new GridBagConstraints(0, 5, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(new JLabel(NUM_RANDOM_SPLITS_TEXT), new GridBagConstraints(0, 6, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numRandomSplitsTextField, new GridBagConstraints(1, 6, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        addAdditionalFormFields(optionPanel);

        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = false;
                setVisible(false);
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JTextField text = findFirstEmptyField();
                if (text != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(DecisionTreeOptionsDialog.this, text);
                } else if (randomTreeBox.isSelected()
                        && Integer.parseInt(numRandomAttrTextField.getText().trim()) > data.numAttributes() - 1) {

                    JOptionPane.showMessageDialog(DecisionTreeOptionsDialog.this,
                            String.format(RANDOM_ATTRS_EXCEEDED_ERROR_MESSAGE, data.numAttributes() - 1),
                            INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);

                    numRandomAttrTextField.requestFocusInWindow();
                } else {
                    try {
                        optionsSetter.setClassifierOptions();
                        dialogResult = true;
                        setVisible(false);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DecisionTreeOptionsDialog.this,
                                ex.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
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
        minObjTextField.requestFocusInWindow();
    }

    private void addAdditionalFormFields(JPanel optionPanel) {
        if (classifier instanceof CHAID) {
            optionPanel.add(new JLabel(HI_SQUARE_TEXT),
                    new GridBagConstraints(0, 7, 1, 1, 1, 1,
                            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

            final JComboBox<String> values = new JComboBox<>(AlPHA);

            optionsSetter = new OptionsSetter() {
                @Override
                void setFormOptions() {
                    super.setFormOptions();
                    values.setSelectedItem(String.valueOf(((CHAID) classifier).getAlpha()));
                }

                @Override
                void setClassifierOptions() {
                    super.setClassifierOptions();
                    ((CHAID) classifier).setAlpha(Double.valueOf(values.getSelectedItem().toString()));
                }
            };
            optionPanel.add(values, new GridBagConstraints(1, 7, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));
        } else {
            optionsSetter = new OptionsSetter();
        }
    }

    @Override
    public final void showDialog() {
        optionsSetter.setFormOptions();
        super.showDialog();
        minObjTextField.requestFocusInWindow();
    }

    private JTextField findFirstEmptyField() {
        if (GuiUtils.isEmpty(minObjTextField)) {
            return minObjTextField;
        } else if (GuiUtils.isEmpty(maxDepthTextField)) {
            return maxDepthTextField;
        } else if (randomTreeBox.isSelected() && GuiUtils.isEmpty(numRandomAttrTextField)) {
            return numRandomAttrTextField;
        } else if (GuiUtils.isEmpty(numRandomSplitsTextField)) {
            return numRandomSplitsTextField;
        } else {
            return null;
        }
    }

    private class OptionsSetter {

        void setFormOptions() {
            minObjTextField.setText(String.valueOf(classifier.getMinObj()));
            maxDepthTextField.setText(String.valueOf(classifier.getMaxDepth()));
            randomTreeBox.setSelected(classifier.isRandomTree());
            numRandomAttrTextField.setText(String.valueOf(classifier.numRandomAttr()));
            numRandomAttrTextField.setEditable(randomTreeBox.isSelected());
            binaryTreeBox.setSelected(classifier.getUseBinarySplits());
            randomSplitsBox.setSelected(classifier.isUseRandomSplits());
            numRandomSplitsTextField.setText(String.valueOf(classifier.getNumRandomSplits()));
            numRandomSplitsTextField.setEditable(randomSplitsBox.isSelected());
        }

        void setClassifierOptions() {
            classifier.setMinObj(Integer.parseInt(minObjTextField.getText().trim()));
            classifier.setMaxDepth(Integer.parseInt(maxDepthTextField.getText().trim()));
            if (randomTreeBox.isSelected()) {
                classifier.setRandomTree(true);
                classifier.setNumRandomAttr(Integer.parseInt(numRandomAttrTextField.getText().trim()));
            }
            classifier.setUseBinarySplits(binaryTreeBox.isSelected());
            classifier.setUseRandomSplits(randomSplitsBox.isSelected());

            if (classifier.isUseRandomSplits()) {
                try {
                    classifier.setNumRandomSplits(Integer.parseInt(numRandomSplitsTextField.getText().trim()));
                } catch (Exception e) {
                    numRandomSplitsTextField.requestFocusInWindow();
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
