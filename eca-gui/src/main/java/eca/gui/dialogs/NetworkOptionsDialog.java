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
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import eca.neural.functions.ActivationFunctionType;
import eca.text.NumericFormatFactory;
import eca.util.EnumUtils;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

import static javax.swing.SwingConstants.RIGHT;

/**
 * @author Roman Batygin
 */
public class NetworkOptionsDialog extends ClassifierOptionsDialogBase<NeuralNetwork> {

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
            "<html><body>Рекомендуемое число нейронов в скрытом слое:  %d &le; N &le; %d</body></html>";
    private static final int HIDDEN_LAYER_STRING_LENGTH = 200;

    private static final Dimension LABEL_DIM = new Dimension(270, 20);

    private JTextField inNeuronsTextField;
    private JTextField outNeuronsTextField;

    private JTextField hidLayersTextField;
    private JTextField afCoefficientTextField;
    private JTextField estimateTextField;
    private JTextField numItsTextField;
    private JTextField learnSpeedTextField;
    private JTextField momentumTextField;

    private JComboBox<String> activationFunctionsBox;

    private DecimalFormat doubleFormat = NumericFormatFactory.getInstance();
    private DecimalFormat estimateFormat = NumericFormatFactory.getInstance();

    private ActivationFunctionBuilder activationFunctionBuilder = new ActivationFunctionBuilder();

    public NetworkOptionsDialog(Window parent, String title, NeuralNetwork mlp, Instances data) {
        super(parent, title, mlp, data);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.setNumberFormats();
        this.createOptionGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        hidLayersTextField.requestFocusInWindow();
    }

    private void createOptionGUI() {
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });

        okButton.addActionListener(event -> {
            JTextField text = GuiUtils.searchFirstEmptyField(hidLayersTextField, afCoefficientTextField,
                    estimateTextField, numItsTextField, learnSpeedTextField, momentumTextField);
            if (text != null) {
                GuiUtils.showErrorMessageAndRequestFocusOn(NetworkOptionsDialog.this, text);
            } else {
                text = hidLayersTextField;
                try {
                    network().setHiddenLayer(hidLayersTextField.getText().trim());
                    AbstractFunction newActivationFunction = (AbstractFunction) getSelectedActivationFunction();
                    newActivationFunction.setCoefficient(getActivationFunction().getCoefficient());
                    network().setActivationFunction(newActivationFunction);
                    text = afCoefficientTextField;
                    getActivationFunction().setCoefficient(doubleFormat.
                            parse(afCoefficientTextField.getText().trim()).doubleValue());
                    network().setNumIterations(Integer.parseInt(numItsTextField.getText().trim()));
                    text = estimateTextField;
                    network().setMinError(estimateFormat.
                            parse(estimateTextField.getText().trim()).doubleValue());
                    text = learnSpeedTextField;
                    learningAlgorithm().setLearningRate(estimateFormat.
                            parse(learnSpeedTextField.getText().trim()).doubleValue());
                    learningAlgorithm().setMomentum(estimateFormat.
                            parse(momentumTextField.getText().trim()).doubleValue());
                    dialogResult = true;
                    setVisible(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(NetworkOptionsDialog.this,
                            e.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                    text.requestFocusInWindow();
                }
            }
        });

        this.add(createImmutableOptionsPanel(), new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(createHiddenLayerPanel(), new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(createActivationFunctionPanel(), new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(createLearningAlgorithmPanel(), new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

    private JPanel createImmutableOptionsPanel() {
        JPanel immutableOptionsPanel = new JPanel(new GridBagLayout());
        immutableOptionsPanel.setBorder(PanelBorderUtils.createTitledBorder(MAIN_OPTIONS_TITLE));
        //--------------------------------------------------
        inNeuronsTextField = new JTextField(TEXT_FIELD_LENGTH);
        inNeuronsTextField.setEditable(false);
        inNeuronsTextField.setBackground(Color.WHITE);
        outNeuronsTextField = new JTextField(TEXT_FIELD_LENGTH);
        outNeuronsTextField.setEditable(false);
        outNeuronsTextField.setBackground(Color.WHITE);
        //----------------------------------------------------
        JLabel inLabel = new JLabel(IN_LAYER_NEURONS_NUM_TITLE);
        inLabel.setPreferredSize(LABEL_DIM);
        inLabel.setHorizontalAlignment(RIGHT);
        JLabel outLabel = new JLabel(OUT_LAYER_NEURONS_NUM_TITLE);
        outLabel.setPreferredSize(LABEL_DIM);
        outLabel.setHorizontalAlignment(RIGHT);
        //-------------------------------------------------
        immutableOptionsPanel.add(inLabel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        immutableOptionsPanel.add(inNeuronsTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        immutableOptionsPanel.add(outLabel,
                new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        immutableOptionsPanel.add(outNeuronsTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        return immutableOptionsPanel;
    }

    private JPanel createHiddenLayerPanel() {
        JLabel hidLabel = new JLabel(HIDDEN_LAYER_STRUCTURE_TITLE);
        hidLabel.setPreferredSize(LABEL_DIM);
        hidLabel.setHorizontalAlignment(RIGHT);
        String recommendText = String.format(RECOMMENDED_HIDDEN_LAYER_MESSAGE,
                NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data()),
                NeuralNetworkUtil.getMaxNumNeuronsInHiddenLayer(data()));
        hidLayersTextField = new JTextField(TEXT_FIELD_LENGTH);
        hidLayersTextField.setDocument(new ListDocument(HIDDEN_LAYER_STRING_LENGTH));
        hidLayersTextField.setInputVerifier(new ListInputVerifier());
        JPanel hiddenLayerPanel = new JPanel(new GridBagLayout());
        hiddenLayerPanel.setBorder(PanelBorderUtils.createTitledBorder(HIDDEN_LAYER_TITLE));
        hiddenLayerPanel.add(hidLabel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        hiddenLayerPanel.add(hidLayersTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        hiddenLayerPanel.setToolTipText(recommendText);
        return hiddenLayerPanel;
    }

    private JPanel createActivationFunctionPanel() {
        JPanel activationFuncPanel = new JPanel(new GridBagLayout());
        activationFuncPanel.setBorder(PanelBorderUtils.createTitledBorder(ACTIVATION_FUNCTION_TITLE));
        activationFunctionsBox = new JComboBox<>(EnumUtils.getDescriptions(ActivationFunctionType.class));
        JLabel coefficientLabel = new JLabel(COEFFICIENT_TITLE);
        coefficientLabel.setPreferredSize(LABEL_DIM);
        coefficientLabel.setHorizontalAlignment(RIGHT);
        afCoefficientTextField = new JTextField(TEXT_FIELD_LENGTH);
        afCoefficientTextField.setDocument(new DoubleDocument(DOUBLE_FIELD_LENGTH));
        afCoefficientTextField.setInputVerifier(new TextFieldInputVerifier());
        activationFuncPanel.add(activationFunctionsBox, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 10, 0, 0), 0, 0));
        activationFuncPanel.add(coefficientLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        activationFuncPanel.add(afCoefficientTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        return activationFuncPanel;
    }

    private JPanel createLearningAlgorithmPanel() {
        JPanel learningPanel = new JPanel(new GridBagLayout());
        learningPanel.setBorder(PanelBorderUtils.createTitledBorder(LEARNING_ALGORITHM_TITLE));
        estimateTextField = new JTextField(TEXT_FIELD_LENGTH);
        estimateTextField.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        estimateTextField.setInputVerifier(new TextFieldInputVerifier());
        numItsTextField = new JTextField(TEXT_FIELD_LENGTH);
        numItsTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        numItsTextField.setInputVerifier(new TextFieldInputVerifier());
        learnSpeedTextField = new JTextField(TEXT_FIELD_LENGTH);
        learnSpeedTextField.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        learnSpeedTextField.setInputVerifier(new TextFieldInputVerifier());

        momentumTextField = new JTextField(TEXT_FIELD_LENGTH);
        momentumTextField.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        momentumTextField.setInputVerifier(new TextFieldInputVerifier());
        JLabel errLabel = new JLabel(ERROR_TITLE);
        errLabel.setPreferredSize(LABEL_DIM);
        errLabel.setHorizontalAlignment(RIGHT);
        JLabel itsLabel = new JLabel(MAX_ITS_TITLE);
        itsLabel.setPreferredSize(LABEL_DIM);
        itsLabel.setHorizontalAlignment(RIGHT);
        JLabel speedLabel = new JLabel(SPEED_COEFFICIENT_TITLE);
        speedLabel.setPreferredSize(LABEL_DIM);
        speedLabel.setHorizontalAlignment(RIGHT);
        JLabel momentumLabel = new JLabel(MOMENTUM_COEFFICIENT_TITLE);
        speedLabel.setPreferredSize(LABEL_DIM);
        speedLabel.setHorizontalAlignment(RIGHT);
        //------------------------------------------------------------
        learningPanel.add(errLabel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(estimateTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        learningPanel.add(itsLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(numItsTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        learningPanel.add(speedLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(learnSpeedTextField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        learningPanel.add(momentumLabel, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        learningPanel.add(momentumTextField, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        return learningPanel;
    }

    @Override
    public void showDialog() {
        this.setOptions();
        super.showDialog();
        hidLayersTextField.requestFocusInWindow();
    }

    private void setNumberFormats() {
        doubleFormat.setMaximumFractionDigits(INT_FIELD_LENGTH);
        estimateFormat.setMaximumIntegerDigits(0);
        estimateFormat.setMaximumFractionDigits(INT_FIELD_LENGTH);
    }

    private void setOptions() {
        inNeuronsTextField.setText(String.valueOf(network().getNumInNeurons()));
        outNeuronsTextField.setText(String.valueOf(network().getNumOutNeurons()));
        hidLayersTextField.setText(getHiddenLayer());
        afCoefficientTextField.setText(doubleFormat.format(activationFunctionCoefficient()));
        estimateTextField.setText(estimateFormat.format(minError()));
        numItsTextField.setText(String.valueOf(maxIts()));
        learnSpeedTextField.setText(estimateFormat.format(learningSpeed()));
        momentumTextField.setText(estimateFormat.format(momentum()));
        ActivationFunctionType activationFunctionType = getActivationFunction().getActivationFunctionType();
        activationFunctionsBox.setSelectedItem(activationFunctionType.getDescription());
    }

    private ActivationFunction getSelectedActivationFunction() {
        ActivationFunctionType activationFunctionType =
                EnumUtils.fromDescription(activationFunctionsBox.getSelectedItem().toString(),
                        ActivationFunctionType.class);
        return activationFunctionType.handle(activationFunctionBuilder);
    }

    private MultilayerPerceptron network() {
        return classifier().getMultilayerPerceptron();
    }

    private String getHiddenLayer() {
        return network().getHiddenLayer();
    }

    private double minError() {
        return network().getMinError();
    }

    private int maxIts() {
        return network().getNumIterations();
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
