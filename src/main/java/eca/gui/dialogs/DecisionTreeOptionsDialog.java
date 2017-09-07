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
 * @author Рома
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
    public static final String RANDOM_SPLITS_TEXT = "Случайные расщепления атрибута";
    public static final String NUM_RANDOM_SPLITS_TEXT = "Число случайных расщеплений:";

    private Setter setter = new Setter();

    private JTextField minObjText;
    private JTextField maxDepthText;
    private JCheckBox randomBox;
    private JTextField numRandomAttrText;
    private JCheckBox binaryTreeBox;
    private JCheckBox randomSplitsBox;
    private JTextField numRandomSplitsText;

    public DecisionTreeOptionsDialog(Window parent, String title,
                                     DecisionTreeClassifier tree, Instances data) {
        super(parent, title, tree, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);

        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(TREE_OPTIONS_MESSAGE));
        minObjText = new JTextField(TEXT_FIELD_LENGTH);
        minObjText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        minObjText.setInputVerifier(new TextFieldInputVerifier());
        maxDepthText = new JTextField(TEXT_FIELD_LENGTH);
        maxDepthText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        maxDepthText.setInputVerifier(new TextFieldInputVerifier());

        randomBox = new JCheckBox(RANDOM_TREE_MESSAGE);

        numRandomAttrText = new JTextField(TEXT_FIELD_LENGTH);
        numRandomAttrText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));

        randomBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                numRandomAttrText.setEditable(randomBox.isSelected());
            }
        });

        binaryTreeBox = new JCheckBox(BINARY_TREE_TYPE_TEXT);

        randomSplitsBox = new JCheckBox(RANDOM_SPLITS_TEXT);

        numRandomSplitsText = new JTextField(TEXT_FIELD_LENGTH);
        numRandomSplitsText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));

        randomSplitsBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                numRandomSplitsText.setEditable(randomSplitsBox.isSelected());
            }
        });

        if (classifier instanceof CART) {
            binaryTreeBox.setEnabled(false);
        }

        optionPanel.add(new JLabel(MIN_OBJ_MESSAGE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(minObjText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(MAX_DEPTH_MESSAGE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(maxDepthText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(randomBox, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(new JLabel(NUM_RANDOM_ATTR_MESSAGE), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numRandomAttrText, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(binaryTreeBox, new GridBagConstraints(0, 4, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(randomSplitsBox, new GridBagConstraints(0, 5, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(new JLabel(NUM_RANDOM_SPLITS_TEXT), new GridBagConstraints(0, 6, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numRandomSplitsText, new GridBagConstraints(1, 6, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));


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
                JTextField text = emptyField();
                if (text != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(DecisionTreeOptionsDialog.this, text);
                } else if (randomBox.isSelected()
                        && Integer.parseInt(numRandomAttrText.getText()) > data.numAttributes() - 1) {

                    JOptionPane.showMessageDialog(DecisionTreeOptionsDialog.this,
                            String.format(RANDOM_ATTRS_EXCEEDED_ERROR_MESSAGE, data.numAttributes() - 1),
                            INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);

                    numRandomAttrText.requestFocusInWindow();
                } else {
                    classifier.setMinObj(Integer.parseInt(minObjText.getText()));
                    classifier.setMaxDepth(Integer.parseInt(maxDepthText.getText()));
                    if (randomBox.isSelected()) {
                        classifier.setRandomTree(true);
                        classifier.setNumRandomAttr(Integer.parseInt(numRandomAttrText.getText()));
                    }
                    classifier.setUseBinarySplits(binaryTreeBox.isSelected());
                    classifier.setUseRandomSplits(randomSplitsBox.isSelected());

                    if (classifier.isUseRandomSplits()) {
                        try {
                            classifier.setNumRandomSplits(Integer.parseInt(numRandomSplitsText.getText()));
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(DecisionTreeOptionsDialog.this,
                                    e.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                            numRandomSplitsText.requestFocusInWindow();
                            return;
                        }
                    }

                    dialogResult = true;
                    setVisible(false);
                }
            }
        });

        if (classifier instanceof CHAID) {
            optionPanel.add(new JLabel(HI_SQUARE_TEXT),
                    new GridBagConstraints(0, 7, 1, 1, 1, 1,
                            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

            final JComboBox<String> values = new JComboBox<>(AlPHA);
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    ((CHAID) classifier).setAlpha(Double.valueOf(values.getSelectedItem().toString()));
                }
            });
            setter = new Setter() {
                @Override
                void setOptions() {
                    super.setOptions();
                    values.setSelectedItem(String.valueOf(((CHAID) classifier).getAlpha()));
                }
            };
            optionPanel.add(values, new GridBagConstraints(1, 7, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));
        }

        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));

        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
        minObjText.requestFocusInWindow();
    }

    @Override
    public final void showDialog() {
        setter.setOptions();
        super.showDialog();
        minObjText.requestFocusInWindow();
    }

    private JTextField emptyField() {
        if (isEmpty(minObjText)) {
            return minObjText;
        } else if (isEmpty(maxDepthText)) {
            return maxDepthText;
        } else if (randomBox.isSelected() && isEmpty(numRandomAttrText)) {
            return numRandomAttrText;
        } else if (isEmpty(numRandomSplitsText)) {
            return numRandomSplitsText;
        } else {
            return null;
        }
    }

    /**
     *
     */
    protected class Setter {

        void setOptions() {
            minObjText.setText(String.valueOf(classifier.getMinObj()));
            maxDepthText.setText(String.valueOf(classifier.getMaxDepth()));
            randomBox.setSelected(classifier.isRandomTree());
            numRandomAttrText.setText(String.valueOf(classifier.numRandomAttr()));
            numRandomAttrText.setEditable(randomBox.isSelected());
            binaryTreeBox.setSelected(classifier.getUseBinarySplits());
            randomSplitsBox.setSelected(classifier.isUseRandomSplits());
            numRandomSplitsText.setText(String.valueOf(classifier.getNumRandomSplits()));
            numRandomSplitsText.setEditable(randomSplitsBox.isSelected());
        }
    }

}
