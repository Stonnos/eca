package eca.gui.dialogs;

import eca.client.dto.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.LengthDocument;
import eca.gui.validators.TextFieldInputVerifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Experiment request dialog.
 * @author Roman Batygin
 */
public class ExperimentRequestDialog extends JDialog {

    private static final int TEXT_LENGTH = 20;
    private static final int FIELD_LENGTH = 255;

    private static final String TITLE = "Создание заявки на эксперимент";
    private static final String FIRST_NAME_TEXT = "Ваше имя:";
    private static final String EMAIL_TEXT = "Ваш email:";
    private static final String CLASSIFIER_TEXT = "Классификатор:";
    private static final String EVALUATION_METHOD_TITLE = "Метод оценки точности";
    private static final String MAIN_OPTIONS_TITLE = "Основные параметры";

    private JTextField firstNameTextField;
    private JTextField emailTextField;
    private JComboBox<String> experimentTypeBox;
    private JRadioButton useTrainingDataRadioButton;
    private JRadioButton crossValidationRadioButton;

    private boolean dialogResult;

    public ExperimentRequestDialog(JFrame parent) {
        super(parent, TITLE, true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.makeGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public boolean isDialogResult() {
        return dialogResult;
    }

    private void makeGUI() {
        JPanel mainOptionPanel = new JPanel(new GridBagLayout());
        mainOptionPanel.setBorder(PanelBorderUtils.createTitledBorder(MAIN_OPTIONS_TITLE));

        firstNameTextField = new JTextField(TEXT_LENGTH);
        firstNameTextField.setDocument(new LengthDocument(FIELD_LENGTH));
        firstNameTextField.setInputVerifier(new TextFieldInputVerifier());

        emailTextField = new JTextField(TEXT_LENGTH);
        emailTextField.setDocument(new LengthDocument(FIELD_LENGTH));
        emailTextField.setInputVerifier(new TextFieldInputVerifier());

        experimentTypeBox = new JComboBox<>(ExperimentType.getDescriptions());

        ButtonGroup group = new ButtonGroup();
        useTrainingDataRadioButton = new JRadioButton(EvaluationMethod.TRAINING_DATA.getDescription());
        crossValidationRadioButton = new JRadioButton(EvaluationMethod.CROSS_VALIDATION.getDescription());
        group.add(useTrainingDataRadioButton);
        group.add(crossValidationRadioButton);
        useTrainingDataRadioButton.setSelected(true);

        JPanel evaluationMethodPanel = new JPanel(new GridBagLayout());
        evaluationMethodPanel.setBorder(PanelBorderUtils.createTitledBorder(EVALUATION_METHOD_TITLE));

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

            }
        });

        mainOptionPanel.add(new JLabel(FIRST_NAME_TEXT), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        mainOptionPanel.add(firstNameTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        mainOptionPanel.add(new JLabel(EMAIL_TEXT), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        mainOptionPanel.add(emailTextField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
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

    }
}
