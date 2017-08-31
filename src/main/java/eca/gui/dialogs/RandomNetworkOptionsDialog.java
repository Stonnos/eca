package eca.gui.dialogs;

import eca.ensemble.RandomNetworks;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.EstimateDocument;
import eca.gui.text.IntegerDocument;
import eca.gui.text.NumericFormat;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class RandomNetworkOptionsDialog extends BaseOptionsDialog<RandomNetworks> {

    private static final int FIELD_LENGTH = 5;

    private static final String OPTIONS_TITLE = "Параметры сетей";
    private static final String ITS_NUM_TITLE = "Число итераций:";
    private static final String MAX_ERROR_TITLE = "Макс. допустимая ошибка классификатора:";
    private static final String MIN_ERROR_TITLE = "Мин. допустимая ошибка классификатора:";
    private static final String USE_BOOTSTRAP_SAMPLE_TEXT = "Использование бутстрэп - выборок";

    private final DecimalFormat estimateFormat = NumericFormat.getInstance();

    private JTextField numClassifiersText;
    private JTextField classifierMinErrorText;
    private JTextField classifierMaxErrorText;
    private JCheckBox useBootstrapSamplesCheckBox;

    public RandomNetworkOptionsDialog(Window parent, String title,
                                     RandomNetworks randomNetworks, Instances data) {
        super(parent, title, randomNetworks, data);
        this.setResizable(false);
        this.createFormat();
        this.makeGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void showDialog() {
        this.setOptions();
        super.showDialog();
        numClassifiersText.requestFocusInWindow();
    }

    private void createFormat() {
        estimateFormat.setMaximumIntegerDigits(0);
        estimateFormat.setMaximumFractionDigits(FIELD_LENGTH);
    }

    private void setOptions() {
        numClassifiersText.setText(String.valueOf(classifier.getIterationsNum()));
        classifierMaxErrorText.setText(estimateFormat.format(classifier.getMaxError()));
        classifierMinErrorText.setText(estimateFormat.format(classifier.getMinError()));
        useBootstrapSamplesCheckBox.setSelected(classifier.isUseBootstrapSamples());
    }

    private void makeGUI() {
        this.setLayout(new GridBagLayout());
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(OPTIONS_TITLE));
        numClassifiersText = new JTextField(TEXT_FIELD_LENGTH);
        numClassifiersText.setDocument(new IntegerDocument(FIELD_LENGTH));
        classifierMinErrorText = new JTextField(TEXT_FIELD_LENGTH);
        classifierMinErrorText.setDocument(new EstimateDocument(FIELD_LENGTH));
        classifierMaxErrorText = new JTextField(TEXT_FIELD_LENGTH);
        classifierMaxErrorText.setDocument(new EstimateDocument(FIELD_LENGTH));

        useBootstrapSamplesCheckBox = new JCheckBox(USE_BOOTSTRAP_SAMPLE_TEXT);

        optionPanel.add(new JLabel(ITS_NUM_TITLE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numClassifiersText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        optionPanel.add(new JLabel(MIN_ERROR_TITLE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMinErrorText, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(MAX_ERROR_TITLE), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(classifierMaxErrorText, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(useBootstrapSamplesCheckBox, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 20, 10), 0, 0));

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
                JTextField textField = GuiUtils.searchFirstEmptyField(numClassifiersText, classifierMinErrorText,
                        classifierMaxErrorText);
                if (textField != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(RandomNetworkOptionsDialog.this, textField);
                } else {
                    try {
                        textField = numClassifiersText;
                        classifier.setIterationsNum(Integer.parseInt(numClassifiersText.getText()));
                        textField = classifierMinErrorText;
                        classifier.setMinError(estimateFormat
                                .parse(classifierMinErrorText.getText()).doubleValue());
                        textField = classifierMaxErrorText;
                        classifier.setMaxError(estimateFormat
                                .parse(classifierMaxErrorText.getText()).doubleValue());
                        classifier.setUseBootstrapSamples(useBootstrapSamplesCheckBox.isSelected());
                        dialogResult = true;
                        setVisible(false);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(RandomNetworkOptionsDialog.this,
                                e.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                        textField.requestFocusInWindow();
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
    }
}
