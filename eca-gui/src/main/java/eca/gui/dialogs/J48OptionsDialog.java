package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.text.IntegerDocument;
import eca.gui.validators.TextFieldInputVerifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for setting {@link J48} algorithm options.
 *
 * @author Roman Batygin
 */
public class J48OptionsDialog extends BaseOptionsDialog<J48> {

    private static final String PRUNING_TREE_TEXT = "Усечение дерева";
    private static final String BINARY_TREE_TEXT = "Бинарное дерево";
    private static final String TREE_OPTIONS_MESSAGE = "Настройки дерева";
    private static final String MIN_OBJ_MESSAGE = "Минимальное число объектов в листе:";
    private static final String BLOCKS_NUM_TITLE = "Количество блоков:";
    private JTextField minObjTextField;
    private JCheckBox useBinarySplitsBox;
    private JCheckBox usePruningBox;
    private JSpinner foldsSpinner;

    public J48OptionsDialog(Window parent, String title, J48 j48, Instances data) {
        super(parent, title, j48, data);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        init();
        this.pack();
        this.setLocationRelativeTo(parent);
        minObjTextField.requestFocusInWindow();
    }

    private void init() {
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(TREE_OPTIONS_MESSAGE));
        minObjTextField = new JTextField(TEXT_FIELD_LENGTH);
        minObjTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        minObjTextField.setInputVerifier(new TextFieldInputVerifier());

        useBinarySplitsBox = new JCheckBox(BINARY_TREE_TEXT);
        usePruningBox = new JCheckBox(PRUNING_TREE_TEXT);
        usePruningBox.addItemListener(evt -> foldsSpinner.setEnabled(usePruningBox.isSelected()));

        foldsSpinner = new JSpinner();
        foldsSpinner.setModel(
                new SpinnerNumberModel(classifier.getNumFolds(), CommonDictionary.MINIMUM_NUMBER_OF_FOLDS,
                        CommonDictionary.MAXIMUM_NUMBER_OF_FOLDS, 1));

        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(evt -> {
            dialogResult = false;
            setVisible(false);
        });

        okButton.addActionListener(evt -> {
            if (GuiUtils.isEmpty(minObjTextField)) {
                GuiUtils.showErrorMessageAndRequestFocusOn(J48OptionsDialog.this, minObjTextField);
            } else {
                classifier.setBinarySplits(useBinarySplitsBox.isSelected());
                classifier.setUnpruned(!usePruningBox.isSelected());
                if (!classifier.getUnpruned()) {
                    classifier.setNumFolds(((SpinnerNumberModel) foldsSpinner.getModel()).getNumber().intValue());
                }
                dialogResult = true;
                setVisible(false);
            }
        });

        optionPanel.add(new JLabel(MIN_OBJ_MESSAGE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(minObjTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(useBinarySplitsBox, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(usePruningBox, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 15, 0, 10), 0, 0));
        optionPanel.add(new JLabel(BLOCKS_NUM_TITLE), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(foldsSpinner, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

    @Override
    public final void showDialog() {
        this.setOptions();
        super.showDialog();
        minObjTextField.requestFocusInWindow();
    }

    private void setOptions() {
        minObjTextField.setText(String.valueOf(classifier.getMinNumObj()));
        useBinarySplitsBox.setSelected(classifier.getBinarySplits());
        usePruningBox.setSelected(!classifier.getUnpruned());
        foldsSpinner.setValue(classifier.getNumFolds());
    }
}
