/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.neural.functions.SineFunction;
import eca.neural.functions.TanhFunction;
import eca.neural.functions.LogisticFunction;
import eca.neural.functions.ExponentialFunction;
import eca.neural.functions.ActivationFunction;
import eca.neural.functions.AbstractFunction;
import eca.gui.text.EstimateDocument;
import eca.gui.text.IntegerDocument;
import eca.gui.text.DoubleDocument;
import eca.gui.text.ListDocument;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import eca.neural.NeuralNetwork;
import eca.neural.MultilayerPerceptron;
import eca.neural.BackPropagation;
import eca.gui.PanelBorderUtils;
import java.text.DecimalFormat;
import weka.core.Instances;
import eca.gui.text.NumericFormat;

/**
 *
 * @author Рома
 */
public class NetworkOptionsDialog extends BaseOptionsDialog<NeuralNetwork> {

    private static final int DOUBLE_FIELD_LENGTH = 12;

    private static final String mainOptionsTitle = "Основные параметры";

    private static final String inLayerNeuronsNumTitle = "Количество нейронов во входном слое:";

    private static final String outLayerNeuronsNumTitle = "Количество нейронов в выходном слое:";

    private static final String hiddenLayerTitle = "Параметры скрытого слоя";

    private static final String hiddenLayerStructureTitle = "Структура скрытого слоя:";

    private static final String activationFunctionTitle = "Активационная функция нейронов скрытого слоя";

    private static final String learningAlgorithmTitle = "Параметры алгоритма обучения";

    private static final String coefficientTitle = "Значение коэффициента:";

    private static final String[] activationsFuctions = {"Логистическая",
                                                        "Гиперболический тангенс",
                                                        "Тригонометрический синус",
                                                        "Экспоненциальная"};

    private static final String[] activationFuctionsToolTipsMessages = {"f(S)=1/(1+exp(-a*S))",
                                                                        "f(S)=(exp(a*S)-exp(-a*S))/(exp(a*S)+exp(-a*S))",
                                                                        "f(S)=sin(a*S)",
                                                                        "f(S)=exp(-S^2/a^2)"};

    private static final String errorTitle = "Допустимая ошибка:";

    private static final String maxItsTitle = "Максимальное число итераций:";

    private static final String speedCoefficientTitle = "Коэффициент скорости обучения:";

    private static final String momentumCoefficientTitle = "Коэффициент момента:";

    private JTextField inNeuronsText;
    private JTextField outNeuronsText;

    private JTextField hidLayersText;
    private JTextField afCoeffText;
    private JTextField estimateText;
    private JTextField numItsText;
    private JTextField learnSpeedText;
    private JTextField momentumText;

    private JRadioButton logistic;
    private JRadioButton tanh;
    private JRadioButton sine;
    private JRadioButton exp;

    private DecimalFormat doubleFormat = NumericFormat.getInstance();
    private DecimalFormat estimateFormat = NumericFormat.getInstance();

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
        defaultPanel.setBorder(PanelBorderUtils.createTitledBorder(mainOptionsTitle));
        //--------------------------------------------------
        inNeuronsText = new JTextField(TEXT_FIELD_LENGTH);
        inNeuronsText.setEditable(false);
        inNeuronsText.setBackground(Color.WHITE);
        outNeuronsText = new JTextField(TEXT_FIELD_LENGTH);
        outNeuronsText.setEditable(false);
        outNeuronsText.setBackground(Color.WHITE);
        //----------------------------------------------------
        JLabel inLabel = new JLabel(inLayerNeuronsNumTitle);
        inLabel.setPreferredSize(labelDim);
        inLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel outLabel = new JLabel(outLayerNeuronsNumTitle);
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
        hiddenLayerPanel.setBorder(PanelBorderUtils.createTitledBorder(hiddenLayerTitle));
        JPanel actFuncPanel = new JPanel(new GridBagLayout());
        actFuncPanel.setBorder(PanelBorderUtils.createTitledBorder(activationFunctionTitle));
        JPanel learningPanel = new JPanel(new GridBagLayout());
        learningPanel.setBorder(PanelBorderUtils.createTitledBorder(learningAlgorithmTitle));
        //-------------------------------------------------------------
        hidLayersText = new JTextField(TEXT_FIELD_LENGTH);
        hidLayersText.setDocument(new ListDocument(200));
        //----------------------------------------------------
        JLabel hidLabel = new JLabel(hiddenLayerStructureTitle);
        hidLabel.setPreferredSize(labelDim);
        hidLabel.setHorizontalAlignment(JLabel.RIGHT);
        String reccomendText = "Рекомендуемое число нейронов"
                + " в скрытом слое:  " + classifier.getMinNumNeuronsInHiddenLayer() + " <= N <= "
                + classifier.getMaxNumNeuronsInHiddenLayer();
        //-------------------------------------------------
        hiddenLayerPanel.add(hidLabel,
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        hiddenLayerPanel.add(hidLayersText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        hiddenLayerPanel.setToolTipText(reccomendText);
        //-------------------------------------------------------------
        ButtonGroup group = new ButtonGroup();
        logistic = new JRadioButton(activationsFuctions[0]);
        tanh = new JRadioButton(activationsFuctions[1]);
        sine = new JRadioButton(activationsFuctions[2]);
        exp = new JRadioButton(activationsFuctions[3]);
        logistic.setToolTipText(activationFuctionsToolTipsMessages[0]);
        tanh.setToolTipText(activationFuctionsToolTipsMessages[1]);
        sine.setToolTipText(activationFuctionsToolTipsMessages[2]);
        exp.setToolTipText(activationFuctionsToolTipsMessages[3]);
        group.add(logistic);
        group.add(tanh);
        group.add(sine);
        group.add(exp);
        JLabel coeffLabel = new JLabel(coefficientTitle);
        coeffLabel.setPreferredSize(labelDim);
        coeffLabel.setHorizontalAlignment(JLabel.RIGHT);
        afCoeffText = new JTextField(TEXT_FIELD_LENGTH);
        afCoeffText.setDocument(new DoubleDocument(DOUBLE_FIELD_LENGTH));
        //--------------------------------------------------------
        actFuncPanel.add(logistic, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        actFuncPanel.add(tanh, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        actFuncPanel.add(sine, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        actFuncPanel.add(exp, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        actFuncPanel.add(coeffLabel, new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        actFuncPanel.add(afCoeffText, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //-------------------------------------------------------------
        estimateText = new JTextField(TEXT_FIELD_LENGTH);
        estimateText.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        numItsText = new JTextField(TEXT_FIELD_LENGTH);
        numItsText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        learnSpeedText = new JTextField(TEXT_FIELD_LENGTH);
        learnSpeedText.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        momentumText = new JTextField(TEXT_FIELD_LENGTH);
        momentumText.setDocument(new EstimateDocument(INT_FIELD_LENGTH));
        //------------------------------------------------------------
        JLabel errLabel = new JLabel(errorTitle);
        errLabel.setPreferredSize(labelDim);
        errLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel itsLabel = new JLabel(maxItsTitle);
        itsLabel.setPreferredSize(labelDim);
        itsLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel speedLabel = new JLabel(speedCoefficientTitle);
        speedLabel.setPreferredSize(labelDim);
        speedLabel.setHorizontalAlignment(JLabel.RIGHT);
        JLabel momentumLabel = new JLabel(momentumCoefficientTitle);
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
                JTextField text = GuiUtils.searchFirstEmptyField(hidLayersText, afCoeffText,
                        estimateText, numItsText, learnSpeedText, momentumText);
                if (text != null) {
                    JOptionPane.showMessageDialog(NetworkOptionsDialog.this,
                            "Заполните все поля!",
                            "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                    text.requestFocusInWindow();
                } else {
                    text = hidLayersText;
                    try {
                        network().setHiddenLayer(hidLayersText.getText());
                        network().setActivationFunction(selectedActFunc());
                        network().setMaxIterationsNum(Integer.parseInt(numItsText.getText()));
                        text = afCoeffText;
                        actFunction().setCoefficient(doubleFormat.
                                parse(afCoeffText.getText()).doubleValue());
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
                                e.getMessage(), "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
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

    public MultilayerPerceptron network() {
        return classifier.network();
    }

    public int numHiddenLayers() {
        return network().hiddenLayersNum();
    }

    public String getHiddenLayer() {
        return network().getHiddenLayer();
    }

    public double minError() {
        return network().getMinError();
    }

    public int maxIts() {
        return network().getMaxIterationsNum();
    }

    public BackPropagation learningAlgorithm() {
        return (BackPropagation) network().getLearningAlgorithm();
    }

    public AbstractFunction actFunction() {
        return (AbstractFunction) network().getActivationFunction();
    }

    public double learningSpeed() {
        return learningAlgorithm().getLearningRate();
    }

    public double momentum() {
        return learningAlgorithm().getMomentum();
    }

    public double actFuncCoefficient() {
        return actFunction().getCoefficient();
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
        afCoeffText.setText(doubleFormat.format(actFuncCoefficient()));
        estimateText.setText(estimateFormat.format(minError()));
        numItsText.setText(String.valueOf(maxIts()));
        learnSpeedText.setText(estimateFormat.format(learningSpeed()));
        momentumText.setText(estimateFormat.format(momentum()));
        //---------------------------------------------
        String func = actFunction().getClass().getSimpleName();
        switch (func) {
            case "LogisticFunction":
                logistic.setSelected(true);
                break;
            case "TanhFunction":
                tanh.setSelected(true);
                break;
            case "SineFunction":
                sine.setSelected(true);
                break;
            case "ExponentialFunction":
                exp.setSelected(true);
                break;
        }
    }

    private ActivationFunction selectedActFunc() {
        if (logistic.isSelected()) {
            return new LogisticFunction();
        } else if (tanh.isSelected()) {
            return new TanhFunction();
        } else if (sine.isSelected()) {
            return new SineFunction();
        } else {
            return new ExponentialFunction();
        }
    }

}
