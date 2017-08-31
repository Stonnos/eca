package eca.gui.dialogs;

import eca.generators.DataGenerator;
import eca.generators.SimpleDataGenerator;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.validators.TextFieldInputVerifier;
import eca.gui.text.IntegerDocument;
import eca.gui.text.LengthDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */

public class DataGeneratorDialog extends JDialog {

    private static final int TEXT_LENGTH = 12;
    private static final String TITLE = "Генерация выборки";
    private static final String GENERATOR_PARAMS_TEXT = "Параметры генератора";
    private static final String ATTR_NUMBER_TEXT = "Число атрибутов:";
    private static final String CLASSES_NUMBER_TEXT = "Число классов:";
    private static final String INSTANCES_NUMBER_TEXT = "Число объектов:";

    private JTextField numAttributesField;
    private JTextField numClassesField;
    private JTextField numInstancesField;

    private SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();

    private boolean dialogResult;

    public DataGeneratorDialog(Frame parent) {
        super(parent, TITLE, true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.makeGUI();
        //-----------------------------------
        this.pack();
        this.setLocationRelativeTo(parent);
        numAttributesField.requestFocusInWindow();
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public DataGenerator getDataGenerator() {
        simpleDataGenerator.setNumAttributes(Integer.valueOf(numAttributesField.getText()));
        simpleDataGenerator.setNumClasses(Integer.valueOf(numClassesField.getText()));
        simpleDataGenerator.setNumInstances(Integer.valueOf(numInstancesField.getText()));
        return simpleDataGenerator;
    }

    private void makeGUI() {
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(GENERATOR_PARAMS_TEXT));

        numAttributesField = new JTextField(TEXT_LENGTH);
        numAttributesField.setDocument(new LengthDocument(4));
        numAttributesField.setInputVerifier(new TextFieldInputVerifier());

        numClassesField = new JTextField(TEXT_LENGTH);
        numClassesField.setDocument(new IntegerDocument(2));
        numClassesField.setInputVerifier(new TextFieldInputVerifier());

        numInstancesField = new JTextField(TEXT_LENGTH);
        numInstancesField.setDocument(new LengthDocument(8));
        numInstancesField.setInputVerifier(new TextFieldInputVerifier());

        optionPanel.add(new JLabel(ATTR_NUMBER_TEXT), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numAttributesField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(CLASSES_NUMBER_TEXT), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numClassesField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(INSTANCES_NUMBER_TEXT), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numInstancesField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //------------------------------------
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
                JTextField field =
                        GuiUtils.searchFirstEmptyField(numAttributesField, numClassesField, numInstancesField);
                if (field != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(DataGeneratorDialog.this, field);
                } else {
                    dialogResult = true;
                    setVisible(false);
                }
            }
        });
        //------------------------------------
        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        setOptions();
        this.getRootPane().setDefaultButton(okButton);
    }

    private void setOptions() {
        numAttributesField.setText(String.valueOf(simpleDataGenerator.getNumAttributes()));
        numClassesField.setText(String.valueOf(simpleDataGenerator.getNumClasses()));
        numInstancesField.setText(String.valueOf(simpleDataGenerator.getNumInstances()));
    }
}
