package eca.gui.dialogs;

import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.LengthDocument;
import eca.gui.validators.EmailValidator;
import eca.gui.validators.Validator;
import eca.util.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Experiment request dialog.
 *
 * @author Roman Batygin
 */
public class ExperimentRequestDialog extends JDialog {

    private static final int TEXT_LENGTH = 15;
    private static final int EMAIL_MAX_LENGTH = 50;

    private static final String TITLE_TEXT = "Создание заявки на эксперимент";
    private static final String EMAIL_TEXT = "Ваш e-mail:";
    private static final String CLASSIFIER_TEXT = "Классификатор:";
    private static final String EVALUATION_METHOD_TITLE = "Метод оценки точности";
    private static final String MAIN_OPTIONS_TITLE = "Основные параметры";
    private static final String INPUT_ERROR_MESSAGE = "Ошибка ввода";
    private static final String INVALID_EMAIL_FORMAT_ERROR = "Введен некорректный e-mail!";
    private static final String EMAIL_FIELD_TOOLTIP_TEXT = "Укажите Email для получения результатов эксперимента";

    private JTextField emailTextField;
    private JComboBox<String> experimentTypeBox;
    private ButtonGroup evaluationMethodsGroup;

    private Validator emailValidator = new EmailValidator();

    private boolean dialogResult;

    public ExperimentRequestDialog(JFrame parent) {
        super(parent, TITLE_TEXT, true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.createGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        emailTextField.requestFocusInWindow();
    }

    public boolean isDialogResult() {
        return dialogResult;
    }

    public void showDialog(ExperimentRequestDto experimentRequestDto) {
        if (experimentRequestDto != null) {
            emailTextField.setText(experimentRequestDto.getEmail());
        }
        setVisible(true);
    }

    private void createGUI() {
        JPanel mainOptionPanel = new JPanel(new GridBagLayout());
        mainOptionPanel.setBorder(PanelBorderUtils.createTitledBorder(MAIN_OPTIONS_TITLE));

        emailTextField = new JTextField(TEXT_LENGTH);
        emailTextField.setDocument(new LengthDocument(EMAIL_MAX_LENGTH));
        emailTextField.setToolTipText(EMAIL_FIELD_TOOLTIP_TEXT);

        experimentTypeBox = new JComboBox<>(EnumUtils.getDescriptions(ExperimentType.class));

        evaluationMethodsGroup = new ButtonGroup();
        JRadioButton useTrainingDataRadioButton = new JRadioButton(EvaluationMethod.TRAINING_DATA.getDescription());
        JRadioButton crossValidationRadioButton = new JRadioButton(EvaluationMethod.CROSS_VALIDATION.getDescription());
        evaluationMethodsGroup.add(useTrainingDataRadioButton);
        evaluationMethodsGroup.add(crossValidationRadioButton);
        useTrainingDataRadioButton.setSelected(true);

        JPanel evaluationMethodPanel = new JPanel(new GridBagLayout());
        evaluationMethodPanel.setBorder(PanelBorderUtils.createTitledBorder(EVALUATION_METHOD_TITLE));

        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(evt -> {
            dialogResult = false;
            setVisible(false);
        });

        okButton.addActionListener(okButtonListener());

        mainOptionPanel.add(new JLabel(EMAIL_TEXT), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        mainOptionPanel.add(emailTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        mainOptionPanel.add(new JLabel(CLASSIFIER_TEXT),
                new GridBagConstraints(0, 2, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        mainOptionPanel.add(experimentTypeBox, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));

        evaluationMethodPanel.add(useTrainingDataRadioButton, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        evaluationMethodPanel.add(crossValidationRadioButton, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));

        this.add(mainOptionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 0, 5), 0, 0));
        this.add(evaluationMethodPanel, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 10, 5), 0, 0));

        this.add(okButton, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

    private ActionListener okButtonListener() {
        return event -> {
            if (!emailValidator.validate(emailTextField.getText().trim())) {
                JOptionPane.showMessageDialog(ExperimentRequestDialog.this,
                        INVALID_EMAIL_FORMAT_ERROR, INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
            } else {
                dialogResult = true;
                setVisible(false);
            }
        };
    }

    public ExperimentRequestDto createExperimentRequestDto() {
        ExperimentRequestDto experimentRequestDto = new ExperimentRequestDto();
        String email = emailTextField.getText().trim();
        if (StringUtils.isNotEmpty(email)) {
            experimentRequestDto.setEmail(email);
        }
        String selectedText = GuiUtils.searchSelectedButtonText(evaluationMethodsGroup);
        EvaluationMethod evaluationMethod = EnumUtils.fromDescription(selectedText, EvaluationMethod.class);
        experimentRequestDto.setEvaluationMethod(evaluationMethod);
        ExperimentType experimentType =
                EnumUtils.fromDescription(experimentTypeBox.getSelectedItem().toString(), ExperimentType.class);
        experimentRequestDto.setExperimentType(experimentType);
        return experimentRequestDto;
    }
}
