/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import java.awt.Window;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import eca.core.TestMethod;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;

/**
 *
 * @author Рома
 */
public class TestingSetOptionsDialog extends JDialog {

    public static final String methodTitle = "Метод оценки точности";
    public static final String initialMethodTitle = "Использование обучающего множества";
    public static final String cvMethodTitle = "V-блочная кросс-проверка";
    public static final String blocksNumTitle = "Количество блоков:";
    public static final String validsNumTitle = "Количество проверок:";

    private JRadioButton useTrainingSet;
    private JRadioButton useTestingSet;
    private JSpinner foldsSpinner = new JSpinner();
    private JSpinner validsSpinner = new JSpinner();

    private boolean dialogResult;
    private int type = TestMethod.TRAINING_SET;
    private int numFolds = 10;
    private int numValids = 10;

    public TestingSetOptionsDialog(Window parent) {
        super(parent, "Настройки");
        this.setModal(true);
        this.setResizable(false);
        this.makeGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public final void setTestingSet(int type) {
        switch (type) {
            case TestMethod.TRAINING_SET:
                useTrainingSet.setSelected(true);
                break;
            case TestMethod.CROSS_VALIDATION:
                useTestingSet.setSelected(true);
                break;
        }
    }

    public final void setParams() {
        type = useTrainingSet.isSelected() ? TestMethod.TRAINING_SET : TestMethod.CROSS_VALIDATION;
        if (useTestingSet.isSelected()) {
            numFolds = ((SpinnerNumberModel) foldsSpinner.getModel()).getNumber().intValue();
            numValids = ((SpinnerNumberModel) validsSpinner.getModel()).getNumber().intValue();
        }
    }

    public void showDialog() {
        setTestingSet(type);
        ((SpinnerNumberModel) foldsSpinner.getModel()).setValue(numFolds);
        ((SpinnerNumberModel) validsSpinner.getModel()).setValue(numValids);
        this.setVisible(true);
    }

    private void makeGUI() {
        this.setLayout(new GridBagLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(PanelBorderUtils.createTitledBorder(methodTitle));
        ButtonGroup group = new ButtonGroup();
        useTrainingSet = new JRadioButton(initialMethodTitle);
        useTestingSet = new JRadioButton(cvMethodTitle);
        group.add(useTrainingSet);
        group.add(useTestingSet);
        //---------------------------------
        foldsSpinner.setModel(new SpinnerNumberModel(numFolds, 2, 100, 1));
        validsSpinner.setModel(new SpinnerNumberModel(numValids, 1, 100, 1));
        foldsSpinner.setEnabled(false);
        validsSpinner.setEnabled(false);
        //--------------------------------
        useTestingSet.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                foldsSpinner.setEnabled(useTestingSet.isSelected());
                validsSpinner.setEnabled(useTestingSet.isSelected());
            }
        });
        //---------------------------------
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
                setParams();
                dialogResult = true;
                setVisible(false);
            }
        });
        //-----------------------------------------------
        panel.add(useTrainingSet, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        panel.add(useTestingSet, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        panel.add(new JLabel(blocksNumTitle), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        panel.add(foldsSpinner, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        panel.add(new JLabel(validsNumTitle), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        panel.add(validsSpinner, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        //--------------------------------------------------------------------
        this.add(panel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

    public final int getTestingSetType() {
        return type;
    }

    public final int numFolds() {
        return numFolds;
    }

    public final int numValids() {
        return numValids;
    }

    public final boolean dialogResult() {
        return dialogResult;
    }

}
