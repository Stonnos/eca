/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.config.ApplicationProperties;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationMethodVisitor;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Evaluation method options dialog.
 *
 * @author Roman Batygin
 */
public class EvaluationMethodOptionsDialog extends JDialog {

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    public static final String METHOD_TITLE = "Метод оценки точности";
    public static final String BLOCKS_NUM_TITLE = "Количество блоков:";
    public static final String TESTS_NUM_TITLE = "Количество проверок:";
    public static final int MINIMUM_NUMBER_OF_FOLDS = 2;
    public static final int MAXIMUM_NUMBER_OF_FOLDS = 100;
    public static final int MINIMUM_NUMBER_OF_TESTS = 1;
    public static final int MAXIMUM_NUMBER_OF_TESTS = 100;
    public static final String OPTIONS_TITLE = "Настройки";

    private JRadioButton useTrainingSetRadioButton;
    private JRadioButton useTestingSetRadioButton;
    private JSpinner foldsSpinner = new JSpinner();
    private JSpinner testsSpinner = new JSpinner();

    private boolean dialogResult;
    private EvaluationMethod evaluationMethod = EvaluationMethod.TRAINING_DATA;
    private int numFolds = APPLICATION_PROPERTIES.getNumFolds();
    private int numTests = APPLICATION_PROPERTIES.getNumTests();

    public EvaluationMethodOptionsDialog(Window parent) {
        super(parent, OPTIONS_TITLE);
        this.setModal(true);
        this.setResizable(false);
        this.makeGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public final void setEvaluationMethod(EvaluationMethod evaluationMethod) {
        evaluationMethod.accept(new EvaluationMethodVisitor<Void>() {
            @Override
            public Void evaluateModel() {
                useTrainingSetRadioButton.setSelected(true);
                return null;
            }

            @Override
            public Void crossValidateModel() {
                useTestingSetRadioButton.setSelected(true);
                return null;
            }
        });
    }

    public final void setParams() {
        evaluationMethod = useTrainingSetRadioButton.isSelected() ?
                EvaluationMethod.TRAINING_DATA : EvaluationMethod.CROSS_VALIDATION;
        if (useTestingSetRadioButton.isSelected()) {
            numFolds = ((SpinnerNumberModel) foldsSpinner.getModel()).getNumber().intValue();
            numTests = ((SpinnerNumberModel) testsSpinner.getModel()).getNumber().intValue();
        }
    }

    public void showDialog() {
        setEvaluationMethod(evaluationMethod);
        foldsSpinner.getModel().setValue(numFolds);
        testsSpinner.getModel().setValue(numTests);
        this.setVisible(true);
    }

    private void makeGUI() {
        this.setLayout(new GridBagLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(PanelBorderUtils.createTitledBorder(METHOD_TITLE));
        ButtonGroup group = new ButtonGroup();
        useTrainingSetRadioButton = new JRadioButton(EvaluationMethod.TRAINING_DATA.getDescription());
        useTestingSetRadioButton = new JRadioButton(EvaluationMethod.CROSS_VALIDATION.getDescription());
        group.add(useTrainingSetRadioButton);
        group.add(useTestingSetRadioButton);
        //---------------------------------
        foldsSpinner.setModel(new SpinnerNumberModel(numFolds, MINIMUM_NUMBER_OF_FOLDS,
                MAXIMUM_NUMBER_OF_FOLDS, 1));
        testsSpinner.setModel(new SpinnerNumberModel(numTests, MINIMUM_NUMBER_OF_TESTS,
                MAXIMUM_NUMBER_OF_TESTS, 1));
        foldsSpinner.setEnabled(false);
        testsSpinner.setEnabled(false);
        //--------------------------------
        useTestingSetRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                foldsSpinner.setEnabled(useTestingSetRadioButton.isSelected());
                testsSpinner.setEnabled(useTestingSetRadioButton.isSelected());
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
        panel.add(useTrainingSetRadioButton, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        panel.add(useTestingSetRadioButton, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        panel.add(new JLabel(BLOCKS_NUM_TITLE), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        panel.add(foldsSpinner, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 10), 0, 0));
        panel.add(new JLabel(TESTS_NUM_TITLE), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
        panel.add(testsSpinner, new GridBagConstraints(1, 3, 1, 1, 1, 1,
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

    public final EvaluationMethod getEvaluationMethod() {
        return evaluationMethod;
    }

    public final int numFolds() {
        return numFolds;
    }

    public final int numTests() {
        return numTests;
    }

    public final boolean dialogResult() {
        return dialogResult;
    }

}
