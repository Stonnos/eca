package eca.gui.dialogs;

import eca.config.ConfigurationService;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.RandomNetworks;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.text.EstimateDocument;
import eca.gui.text.IntegerDocument;
import eca.gui.validators.TextFieldInputVerifier;
import eca.text.NumericFormatFactory;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class RandomNetworkOptionsDialog extends ClassifierOptionsDialogBase<RandomNetworks> {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final int FIELD_LENGTH = 5;

    private static final String OPTIONS_TITLE = "Параметры сетей";
    private static final String ITS_NUM_TITLE = "Число итераций:";
    private static final String MAX_ERROR_TITLE = "Макс. допустимая ошибка классификатора:";
    private static final String MIN_ERROR_TITLE = "Мин. допустимая ошибка классификатора:";
    private static final String USE_BOOTSTRAP_SAMPLE_TEXT = "Использование бутстрэп - выборок";
    private static final String NUM_THREADS_TITLE = "Число потоков:";

    private final DecimalFormat estimateFormat = NumericFormatFactory.getInstance();

    private JTextField numClassifiersTextField;
    private JTextField classifierMinErrorTextField;
    private JTextField classifierMaxErrorTextField;
    private JCheckBox useBootstrapSamplesCheckBox;

    private JSpinner threadsSpinner;

    public RandomNetworkOptionsDialog(Window parent, String title,
                                      RandomNetworks randomNetworks, Instances data) {
        super(parent, title, randomNetworks, data);
        this.setResizable(false);
        this.createFormat();
        this.createGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void showDialog() {
        this.setOptions();
        super.showDialog();
        numClassifiersTextField.requestFocusInWindow();
    }

    private void createFormat() {
        estimateFormat.setMaximumIntegerDigits(0);
        estimateFormat.setMaximumFractionDigits(FIELD_LENGTH);
    }

    private void setOptions() {
        numClassifiersTextField.setText(String.valueOf(classifier.getIterationsNum()));
        classifierMaxErrorTextField.setText(estimateFormat.format(classifier.getMaxError()));
        classifierMinErrorTextField.setText(estimateFormat.format(classifier.getMinError()));
        useBootstrapSamplesCheckBox.setSelected(classifier.isUseBootstrapSamples());
        threadsSpinner.setModel(
                new SpinnerNumberModel(EnsembleUtils.getNumThreads(classifier), CommonDictionary.MIN_THREADS_NUM,
                        CONFIG_SERVICE.getApplicationConfig().getMaxThreads().intValue(), 1));
    }

    private void createGUI() {
        this.setLayout(new GridBagLayout());
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(OPTIONS_TITLE));
        numClassifiersTextField = new JTextField(TEXT_FIELD_LENGTH);
        numClassifiersTextField.setDocument(new IntegerDocument(FIELD_LENGTH));
        numClassifiersTextField.setInputVerifier(new TextFieldInputVerifier());
        classifierMinErrorTextField = new JTextField(TEXT_FIELD_LENGTH);
        classifierMinErrorTextField.setDocument(new EstimateDocument(FIELD_LENGTH));
        classifierMinErrorTextField.setInputVerifier(new TextFieldInputVerifier());
        classifierMaxErrorTextField = new JTextField(TEXT_FIELD_LENGTH);
        classifierMaxErrorTextField.setDocument(new EstimateDocument(FIELD_LENGTH));
        classifierMaxErrorTextField.setInputVerifier(new TextFieldInputVerifier());

        useBootstrapSamplesCheckBox = new JCheckBox(USE_BOOTSTRAP_SAMPLE_TEXT);

        threadsSpinner = new JSpinner();

        optionPanel.add(new JLabel(ITS_NUM_TITLE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numClassifiersTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(MIN_ERROR_TITLE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMinErrorTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(MAX_ERROR_TITLE), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMaxErrorTextField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(useBootstrapSamplesCheckBox, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 20, 10), 0, 0));
        optionPanel.add(new JLabel(NUM_THREADS_TITLE), new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(threadsSpinner, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });
        //-----------------------------------------------
        okButton.addActionListener(event -> {
            JTextField textField = GuiUtils.searchFirstEmptyField(numClassifiersTextField,
                    classifierMinErrorTextField,
                    classifierMaxErrorTextField);
            if (textField != null) {
                GuiUtils.showErrorMessageAndRequestFocusOn(RandomNetworkOptionsDialog.this, textField);
            } else {
                try {
                    textField = numClassifiersTextField;
                    classifier.setIterationsNum(Integer.parseInt(numClassifiersTextField.getText().trim()));
                    textField = classifierMinErrorTextField;
                    classifier.setMinError(estimateFormat
                            .parse(classifierMinErrorTextField.getText().trim()).doubleValue());
                    textField = classifierMaxErrorTextField;
                    classifier.setMaxError(estimateFormat
                            .parse(classifierMaxErrorTextField.getText().trim()).doubleValue());
                    classifier.setUseBootstrapSamples(useBootstrapSamplesCheckBox.isSelected());
                    classifier.setNumThreads(
                            ((SpinnerNumberModel) threadsSpinner.getModel()).getNumber().intValue());
                    dialogResult = true;
                    setVisible(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RandomNetworkOptionsDialog.this,
                            e.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                    textField.requestFocusInWindow();
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
    }
}
