/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.trees.DecisionTreeClassifier;
import eca.trees.CHAID;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import eca.gui.PanelBorderUtils;
import eca.gui.text.IntegerDocument;
import weka.core.*;

/**
 *
 * @author Рома
 */
public class DecisionTreeOptionsDialog extends BaseOptionsDialog<DecisionTreeClassifier> {

    private static final String treeOptionsMessage = "Параметры дерева";
    private static final String randomTreeMessage = "Случайное дерево";
    private static final String minObjMessage = "Минимальное число объектов в листе:";
    private static final String maxDepthMessage = "Максимальная глубина дерева:";
    private static final String numRandomAttrMessage = "Число случайных атрибутов:";

    private Setter setter = new Setter();

    private JTextField minObjText;
    private JTextField maxDepthText;
    private JCheckBox randomBox;
    private JTextField numRandomAttrText;

    public DecisionTreeOptionsDialog(Window parent, String title,
            DecisionTreeClassifier tree, Instances data) {
        super(parent, title, tree, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        //------------------------------------
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(treeOptionsMessage));
        minObjText = new JTextField(TEXT_FIELD_LENGTH);
        minObjText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        maxDepthText = new JTextField(TEXT_FIELD_LENGTH);
        maxDepthText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        //----------------------------------------------------
        randomBox = new JCheckBox(randomTreeMessage);
        //-----------------------------------------------------------
        randomBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                numRandomAttrText.setEditable(randomBox.isSelected());
            }
        });
        //-------------------------------------------------------
        numRandomAttrText = new JTextField(TEXT_FIELD_LENGTH);
        numRandomAttrText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        //-------------------------------------------------------
        optionPanel.add(new JLabel(minObjMessage),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(minObjText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(maxDepthMessage), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(maxDepthText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(randomBox, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
        optionPanel.add(new JLabel(numRandomAttrMessage), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numRandomAttrText, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //-----------------------------------------------
        //------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();
        //-----------------------------------------------
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
                JTextField text = emptyField();
                if (text != null) {
                    JOptionPane.showMessageDialog(DecisionTreeOptionsDialog.this,
                            "Заполните все поля!",
                            "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                    text.requestFocusInWindow();
                } else if (randomBox.isSelected()
                        && Integer.parseInt(numRandomAttrText.getText()) > data.numAttributes() - 1) {
                    JOptionPane.showMessageDialog(DecisionTreeOptionsDialog.this,
                            "Число случайных атрибутов должно быть не больше "
                            + String.valueOf(data.numAttributes() - 1),
                            "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                    numRandomAttrText.requestFocusInWindow();
                } else {
                    classifier.setMinObj(Integer.parseInt(minObjText.getText()));
                    classifier.setMaxDepth(Integer.parseInt(maxDepthText.getText()));
                    if (randomBox.isSelected()) {
                        classifier.setRandomTree(true);
                        classifier.setNumRandomAttr(Integer.parseInt(numRandomAttrText.getText()));
                    }
                    dialogResult = true;
                    setVisible(false);
                }
            }
        });
        //----------------------------------
        if (classifier instanceof CHAID) {
            final JCheckBox binaryBox = new JCheckBox("Бинарное дерево");
            optionPanel.add(binaryBox, new GridBagConstraints(0, 4, 2, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
            optionPanel.add(new JLabel("<html><body>Уровень значимости для<br>статистики хи-квадрат:</body></html>"),
                    new GridBagConstraints(0, 5, 1, 1, 1, 1,
                            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
            String[] alphas = {"0.995","0.99","0.975","0.95","0.75","0.5","0.25",
                "0.1","0.05","0.025","0.01","0.005"};    
            final JComboBox<String> values = new JComboBox<>(alphas);
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    ((CHAID) classifier).setAlpha(Double.valueOf(values.getSelectedItem().toString()));
                    ((CHAID) classifier).setUseBinarySplits(binaryBox.isSelected());
                }
            });
            setter = new Setter() {
                @Override
                void setOptions() {
                    super.setOptions();
                    binaryBox.setSelected(((CHAID) classifier).getUseBinarySplits());
                    values.setSelectedItem(String.valueOf(((CHAID) classifier).getAlpha()));
                }
            };
            optionPanel.add(values, new GridBagConstraints(1, 5, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));
        }
        //------------------------------------
        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
        //-----------------------------------
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
        }
    }

}
