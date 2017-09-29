/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.DoubleDocument;
import eca.gui.text.EstimateDocument;
import eca.gui.text.IntegerDocument;
import eca.gui.text.ListDocument;
import eca.gui.validators.ListInputVerifier;
import eca.gui.validators.TextFieldInputVerifier;
import eca.neural.BackPropagation;
import eca.neural.MultilayerPerceptron;
import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import eca.neural.functions.*;
import eca.text.NumericFormat;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Enumeration;

/**
 * @author Roman Batygin
 */
public class NetworkOptionsDialog extends BaseOptionsDialog<NeuralNetwork> {

    private static final int DOUBLE_FIELD_LENGTH = 12;

    private static final String MAIN_OPTIONS_TITLE = "Основные параметры";

    private static final String IN_LAYER_NEURONS_NUM_TITLE = "Количество нейронов во входном слое:";

    private static final String OUT_LAYER_NEURONS_NUM_TITLE = "Количество нейронов в выходном слое:";

    private static final String HIDDEN_LAYER_TITLE = "Параметры скрытого слоя";

    private static final String HIDDEN_LAYER_STRUCTURE_TITLE = "Структура скрытого слоя:";

    private static final String ACTIVATION_FUNCTION_TITLE = "Активационная функция нейронов скрытого слоя";

    private static final String LEARNING_ALGORITHM_TITLE = "Параметры алгоритма обучения";

    private static final String COEFFICIENT_TITLE = "Значение коэффициента:";

    private static final String ERROR_TITLE = "Допустимая ошибка:";

    private static final String MAX_ITS_TITLE = "Максимальное число итераций:";

    private static final String SPEED_COEFFICIENT_TITLE = "Коэффициент скорости обучения:";

    private static final String MOMENTUM_COEFFICIENT_TITLE = "Коэффициент момента:";
    private static final String RECOMMENDED_HIDDEN_LAYER_MESSAGE =
            "Рекомендуемое число нейронов в скрытом слое:  %d <= N <= %d";
    private static final int HIDDEN_LAYER_STRING_LENGTH = 200;

    private JTextField inNeuronsText;
    private JTextField outNeuronsText;

    private JTextField hidLayersText;
    private JTextField afCoefficientText;
    private JTextField estimateText;
    private JTextField numItsText;
    private JTextField learnSpeedText;
    private JTextField momentumText;

    private ButtonGroup activationFunctionsGroup;
    private JRadioButton logistic;
    private JRadioButton hyperbolicTangent;
    private JRadioButton sine;
    private JRadioButton exp;

    private JComboBox<String> activationFunctionsBox;

    private DecimalFormat doubleFormat = NumericFormat.getInstance();
    private DecimalFormat estimateFormat = NumericFormat.getInstance();

    private ActivationFunctionBuilder activationFunctionBuilder = new ActivationFunctionBuilder();

    public NetworkOptionsDialog(Window parent, String title, NeuralNetwork mlp, Instances data) {
        super(parent, title, mlp, data);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.createFormats();
        this.makeOptionGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        hidLayersText.requestFocusInWindow();
    }

    private void makeOptionGUI() {
        Dimension labelDim = new Dimension(270, 20);
        //---------------------------------------------------
        JPanel defaultPanel = new JPanel(new GridBagLayout());
        defaultPanel.setBorder(PanelBorderUtils.createTitledBorder(MAIN_OPTIONS_TITLE));
        //--------------------------------------------------
        inNeuronsText = new JTextField(TEXT_FIELD_LENGTH);
        inNeuronsText.setEditable(false);
        inNeuronsText.setBackground(Color.WHITE);
        outNeuronsText = new JTextField(TEXT_FIELD_LENGTH);
        outNeuronsText.setEditable(false);
        outNeuronsText.setBackground(Color.WHITE);
        //----------------------------------------------------
        JLabel inLabel = new JLabel(IN_LAYER_NEURONS_NUM_TITLE);
        inLabel.setPreferredSize(labelDim);
        inLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel outLabel = new JLabel(OUT_LAYER_NEURONS_NUM_TITLE);
        outLabel.setPreferredSize(labelDim);
        outLabel.setHorizontalAlignment(JLabel.RIGHT);
        //-------------------------------------------------
        defaultPanel.add(inLabel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        defaultPanel.add(inNeuronsText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        defaultPanel.add(outLabel,
                new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        defaultPanel.add(outNeuronsText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //------------------------------------------------------------
        JPanel hiddenLayerPanel = new JPanel(new GridBagLayout());
        hiddenLayerPanel.setBorder(PanelBorderUtils.createTitledBorder(HIDDEN_LAYER_TITLE));
        JPanel actFuncPanel = new JPanel(new GridBagLayout());
        actFuncPanel.setBorder(PanelBorderUtils.createTitledBorder(ACTIVATION_FUNCTION_TITLE));
        JPanel learningPanel = new JPanel(new GridBagLayout());
        learningPanel.setBorder(PanelBorderUtils.createTitledBorder(LEARNING_ALGORITHM_TITLE));
        //-------------------------------------------------------------
        hidLayersText = new JTextField(TEXT_FIELD_LENGTH);
        hidLayersText.setDocument(new ListDocument(HIDDEN_LAYER_STRING_LENGTH));
        hidLayersText.setInputVerifier(new ListInputVerifier());
        //----------------------------------------------------
        JLabel hidLabel = new JLabel(HIDDEN_LAYER_STRUCTURE_TITLE);
        hidLabel.setPreferredSize(labelDim);
        hidLabel.setHorizontalAlignment(JLabel.RIGHT);
        String recommendText = String.format(RECOMMENDED_HIDDEN_LAYER_MESSAGE,
                NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data()),
                NeuralNetworkUtil.getMaxNumNeuronsInHiddenLayer(data()));
        //-------------------------------------------------
        hiddenLayerPanel.add(hidLabel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        hiddenLayerPanel.add(hidLayersText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        hiddenLayerPanel.setToolTipText(recommendText);
        //-------------------------------------------------------------
        /*activationFunctionsGroup = new ButtonGroup();

        logistic = new JRadioButton(ActivationFunctionType.LOGISTIC.getDescription());
        hyperbolicTangent = new JRadioButton(ActivationFunctionType.HYPERBOLIC_TANGENT.getDescription());
        sine = new JRadioButton(ActivationFunctionType.SINUSOID.getDescription());
        exp = new JRadioButton(ActivationFunctionType.EXPONENTIAL.getDescription());

        logistic.setToolTipText(ActivationFunctionType.LOGISTIC.getFormula());
        hyperbolicTangent.setToolTipText(ActivationFunctionType.HYPERBOLIC_TANGENT.getFormula());
        sine.setToolTipText(ActivationFunctionType.SINUSOID.getFormula());
        exp.setToolTipText(ActivationFunctionType.EXPONENTIAL.getFormula());

        activationFunctionsGroup.add(logistic);
        activationFunctionsGroup.add(hyperbolicTangent);
        activationFunctionsGroup.add(sine);
        activationFunctionsGroup.add(exp);*/

        activationFunctionsBox = new JComboBox(ActivationFunctionType.getDescriptions());

        JLabel coefficientLabel = new JLabel(COEFFICIENT_TITLE);
        coefficientLabel.setPreferredSize(labelDim);
        coefficientLabel.setHorizontalAlignment(JLabel.RIGHT);
        afCoefficientText = new JTextField(TEXT_FIELD_LENGTH);
        afCoefficientText.setDocument(new DoubleDocument(DOUBLE_FIELD_LENGTH));
        afCoefficientText.setInputVerifier(new TextFieldInputVerifier());
        //--------------------------------------------------------
        /*actFuncPanel.add(logistic, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        actFuncPanel.add(hyperbolicTangent, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        actFuncPanel.add(sine, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        actFuncPanel.add(exp, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
                */

        actFuncPanel.add(activationFunctionsBox, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 10, 0, 0), 0, 0));
        actFuncPanel.add(coefficientLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        actFuncPanel.add(afCoefficientText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //-------------------------------------------------------------
        estimateText = new JTextField(TEXT_FIELD_LENGTH);
        estimateText.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        estimateText.setInputVerifier(new TextFieldInputVerifier());
        numItsText = new JTextField(TEXT_FIELD_LENGTH);
        numItsText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        numItsText.setInputVerifier(new TextFieldInputVerifier());
        learnSpeedText = new JTextField(TEXT_FIELD_LENGTH);
        learnSpeedText.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        learnSpeedText.setInputVerifier(new TextFieldInputVerifier());

        momentumText = new JTextField(TEXT_FIELD_LENGTH);
        momentumText.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        momentumText.setInputVerifier(new TextFieldInputVerifier());
        //------------------------------------------------------------
        JLabel errLabel = new JLabel(ERROR_TITLE);
        errLabel.setPreferredSize(labelDim);
        errLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel itsLabel = new JLabel(MAX_ITS_TITLE);
        itsLabel.setPreferredSize(labelDim);
        itsLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel speedLabel = new JLabel(SPEED_COEFFICIENT_TITLE);
        speedLabel.setPreferredSize(labelDim);
        speedLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel momentumLabel = new JLabel(MOMENTUM_COEFFICIENT_TITLE);
        speedLabel.setPreferredSize(labelDim);
        speedLabel.setHorizontalAlignment(JLabel.RIGHT);
        //------------------------------------------------------------
        learningPanel.add(errLabel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(estimateText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        learningPanel.add(itsLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(numItsText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        learningPanel.add(speedLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(learnSpeedText, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        learningPanel.add(momentumLabel, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(momentumText, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //------------------------------------------------------------------
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
                JTextField text = GuiUtils.searchFirstEmptyField(hidLayersText, afCoefficientText,
                        estimateText, numItsText, learnSpeedText, momentumText);
                if (text != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(NetworkOptionsDialog.this, text);
                } else {
                    text = hidLayersText;
                    try {
                        network().setHiddenLayer(hidLayersText.getText());
                        network().setActivationFunction(getSelectedActivationFunction());
                        network().setMaxIterationsNum(Integer.parseInt(numItsText.getText()));
                        text = afCoefficientText;
                        getActivationFunction().setCoefficient(doubleFormat.
                                parse(afCoefficientText.getText()).doubleValue());
                        text = estimateText;
                        network().setMinError(estimateFormat.
                                parse(estimateText.getText()).doubleValue());
                        text = learnSpeedText;
                        learningAlgorithm().setLearningRate(estimateFormat.
                                parse(learnSpeedText.getText()).doubleValue());
                        learningAlgorithm().setMomentum(estimateFormat.
                                parse(momentumText.getText()).doubleValue());
                        dialogResult = true;
                        setVisible(false);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(NetworkOptionsDialog.this,
                                e.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                        text.requestFocusInWindow();
                    }
                }
            }
        });
        //------------------------------------------------------------------
        this.add(defaultPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(hiddenLayerPanel, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(actFuncPanel, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(learningPanel, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

    @Override
    public void showDialog() {
        this.setOptions();
        super.showDialog();
        hidLayersText.requestFocusInWindow();
    }

    private void createFormats() {
        doubleFormat.setMaximumFractionDigits(INT_FIELD_LENGTH);
        estimateFormat.setMaximumIntegerDigits(0);
        estimateFormat.setMaximumFractionDigits(INT_FIELD_LENGTH);
    }

    private void setOptions() {
        inNeuronsText.setText(String.valueOf(network().inLayerNeuronsNum()));
        outNeuronsText.setText(String.valueOf(network().outLayerNeuronsNum()));
        hidLayersText.setText(getHiddenLayer());
        afCoefficientText.setText(doubleFormat.format(activationFunctionCoefficient()));
        estimateText.setText(estimateFormat.format(minError()));
        numItsText.setText(String.valueOf(maxIts()));
        learnSpeedText.setText(estimateFormat.format(learningSpeed()));
        momentumText.setText(estimateFormat.format(momentum()));

        ActivationFunctionType activationFunctionType = getActivationFunction().getActivationFunctionType();
        activationFunctionsBox.setSelectedItem(activationFunctionType.getDescription());

        /*activationFunctionType.handle(new ActivationFunctionTypeVisitor<Void>() {

            @Override
            public Void caseLogistic() {
                logistic.setSelected(true);
                return null;
            }

            @Override
            public Void caseHyperbolicTangent() {
                hyperbolicTangent.setSelected(true);
                return null;
            }

            @Override
            public Void caseSine() {
                sine.setSelected(true);
                return null;
            }

            @Override
            public Void caseExponential() {
                exp.setSelected(true);
                return null;
            }

            @Override
            public Void caseSoftSign() {
                exp.setSelected(true);
                return null;
            }

        });*/

    }

    private ActivationFunction getSelectedActivationFunction() {

        /*for (Enumeration<AbstractButton> buttonEnumeration = activationFunctionsGroup.getElements();
                buttonEnumeration.hasMoreElements();) {

            AbstractButton abstractButton = buttonEnumeration.nextElement();

            if (abstractButton.isSelected()) {

                ActivationFunctionType activationFunctionType =
                        ActivationFunctionType.findByDescription(abstractButton.getText());

                return activationFunctionType.handle(activationFunctionBuilder);
            }
        }*/

        ActivationFunctionType activationFunctionType =
                ActivationFunctionType.findByDescription(activationFunctionsBox.getSelectedItem().toString());

        return activationFunctionType.handle(activationFunctionBuilder);
    }

    private MultilayerPerceptron network() {
        return classifier.network();
    }

    private String getHiddenLayer() {
        return network().getHiddenLayer();
    }

    private double minError() {
        return network().getMinError();
    }

    private int maxIts() {
        return network().getMaxIterationsNum();
    }

    private BackPropagation learningAlgorithm() {
        return (BackPropagation) network().getLearningAlgorithm();
    }

    private AbstractFunction getActivationFunction() {
        return (AbstractFunction) network().getActivationFunction();
    }

    private double learningSpeed() {
        return learningAlgorithm().getLearningRate();
    }

    private double momentum() {
        return learningAlgorithm().getMomentum();
    }

    private double activationFunctionCoefficient() {
        return getActivationFunction().getCoefficient();
    }

}
