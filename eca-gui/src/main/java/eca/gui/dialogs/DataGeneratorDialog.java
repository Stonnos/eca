package eca.gui.dialogs;

import eca.config.ConfigurationService;
import eca.generators.DataGenerator;
import eca.generators.SimpleDataGenerator;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.text.IntegerDocument;
import eca.gui.text.LengthDocument;
import eca.gui.validators.TextFieldInputVerifier;
import eca.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Training data generator dialog.
 *
 * @author Roman Batygin
 */
public class DataGeneratorDialog extends JDialog {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final int TEXT_LENGTH = 12;
    private static final String TITLE_TEXT = "Генерация выборки";
    private static final String RELATION_NAME_TEXT = "Название:";
    private static final String GENERATOR_PARAMS_TEXT = "Параметры генератора";
    private static final String ATTR_NUMBER_TEXT = "Число атрибутов:";
    private static final String CLASSES_NUMBER_TEXT = "Число классов:";
    private static final String INSTANCES_NUMBER_TEXT = "Число объектов:";
    private static final String SEED_TEXT = "Начальное значение (seed):";
    private static final int RELATION_NAME_FIELD_LENGTH = 255;
    private static final int NUM_ATTRS_FIELD_LENGTH = 4;
    private static final int NUM_CLASSES_FIELD_LENGTH = 2;
    private static final int NUM_INSTANCES_FIELD_LENGTH = 8;
    private static final int SEED_FIELD_LENGTH = 12;
    private static final String INPUT_ERROR_MESSAGE = "Ошибка ввода";

    private JTextField relationNameField;
    private JTextField numAttributesField;
    private JTextField numClassesField;
    private JTextField numInstancesField;
    private JTextField seedField;

    private SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();

    private boolean dialogResult;

    public DataGeneratorDialog(Frame parent) {
        super(parent, TITLE_TEXT, true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.createGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        relationNameField.requestFocusInWindow();
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public DataGenerator getDataGenerator() {
        return simpleDataGenerator;
    }

    private void createGUI() {
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(GENERATOR_PARAMS_TEXT));

        relationNameField = new JTextField(TEXT_LENGTH);
        relationNameField.setDocument(new LengthDocument(RELATION_NAME_FIELD_LENGTH));

        numAttributesField = new JTextField(TEXT_LENGTH);
        numAttributesField.setDocument(new IntegerDocument(NUM_ATTRS_FIELD_LENGTH));
        numAttributesField.setInputVerifier(new TextFieldInputVerifier());

        numClassesField = new JTextField(TEXT_LENGTH);
        numClassesField.setDocument(new IntegerDocument(NUM_CLASSES_FIELD_LENGTH));
        numClassesField.setInputVerifier(new TextFieldInputVerifier());

        numInstancesField = new JTextField(TEXT_LENGTH);
        numInstancesField.setDocument(new IntegerDocument(NUM_INSTANCES_FIELD_LENGTH));
        numInstancesField.setInputVerifier(new TextFieldInputVerifier());

        seedField = new JTextField(TEXT_LENGTH);
        seedField.setDocument(new IntegerDocument(SEED_FIELD_LENGTH));

        optionPanel.add(new JLabel(RELATION_NAME_TEXT), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(relationNameField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(ATTR_NUMBER_TEXT), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numAttributesField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(CLASSES_NUMBER_TEXT), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numClassesField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(INSTANCES_NUMBER_TEXT), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numInstancesField, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(SEED_TEXT), new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(seedField, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(evt -> {
            dialogResult = false;
            setVisible(false);
        });

        okButton.addActionListener(evt -> {
            JTextField founded =
                    GuiUtils.searchFirstEmptyField(numAttributesField, numClassesField, numInstancesField);
            if (founded != null) {
                GuiUtils.showErrorMessageAndRequestFocusOn(DataGeneratorDialog.this, founded);
            } else {
                try {
                    setRelationName();
                    founded = numAttributesField;
                    simpleDataGenerator.setNumAttributes(Integer.valueOf(numAttributesField.getText().trim()));
                    founded = numClassesField;
                    simpleDataGenerator.setNumClasses(Integer.valueOf(numClassesField.getText().trim()));
                    founded = numInstancesField;
                    simpleDataGenerator.setNumInstances(Integer.valueOf(numInstancesField.getText().trim()));
                    setSeed();
                    dialogResult = true;
                    setVisible(false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DataGeneratorDialog.this,
                            ex.getMessage(), INPUT_ERROR_MESSAGE, JOptionPane.WARNING_MESSAGE);
                    founded.requestFocusInWindow();
                }
            }
        });

        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));

        setDefaultOptions();
        this.getRootPane().setDefaultButton(okButton);
    }

    private void setRelationName() {
        if (!GuiUtils.isEmpty(relationNameField)) {
            simpleDataGenerator.setRelationName(relationNameField.getText().trim());
        }
    }

    private void setSeed() {
        if (!GuiUtils.isEmpty(seedField)) {
            long seed = Long.parseLong(seedField.getText().trim());
            simpleDataGenerator.setRandom(new Random(seed));
        }
    }

    private void setDefaultOptions() {
        numAttributesField.setText(String.valueOf(simpleDataGenerator.getNumAttributes()));
        numClassesField.setText(String.valueOf(simpleDataGenerator.getNumClasses()));
        numInstancesField.setText(String.valueOf(simpleDataGenerator.getNumInstances()));
        int seed = Utils.getIntValueOrDefault(CONFIG_SERVICE.getApplicationConfig().getSeed(),
                CommonDictionary.MIN_SEED);
        seedField.setText(String.valueOf(seed));
    }
}
