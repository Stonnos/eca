package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.util.Utils;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;

/**
 * Implements contingency table options dialog.
 *
 * @author Roman Batygin
 */
public class ContingencyTableOptionsDialog extends JDialog {

    private static final String TITLE = "Таблица сопряженности (Настройки)";
    private static final double DEFAULT_ALPHA_VALUE = 0.05d;
    private static final double ALPHA_MIN_VALUE = 0.001d;
    private static final double MAX_ALPHA_VALUE = 0.999d;
    private static final double ALPHA_STEP = 0.001d;
    private static final String ROW_ATTR_TEXT = "Атрибут (строки):";
    private static final String COL_ATTR_TEXT = "Атрибут (столбцы):";
    private static final String CHI_SQUARE_TEXT =
            "<html><body>Уровень значимости для теста &chi;&sup2;</body></html>";
    private static final String YATES_CORRECTION_TEXT = "Поправка Йетса";
    private static final String ATTRIBUTES_TEXT = "Атрибуты";
    private static final String CHI_SQUARE_OPTIONS_TEXT =
            "<html><body>Настройки теста &chi;&sup2;</body></html>";
    private static final String INVALID_ATTR_TYPE_ERROR_FORMAT = "Атрибут '%s' не является категориальным атрибутом!";
    private static final String DUPLICATE_ATTRIBUTES_ERROR_MESSAGE = "Необходимо выбрать разные атрибуты!";
    private static final Dimension COMBO_BOX_DIM = new Dimension(250, 25);

    private final Instances data;

    private boolean dialogResult;

    private JComboBox<String> rowAttributeComboBox;
    private JComboBox<String> colAttributeComboBox;
    private JSpinner alphaSpinner;
    private JCheckBox useYatesCheckbox;

    public ContingencyTableOptionsDialog(Frame parent, Instances data) {
        super(parent, TITLE, true);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.init();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public int gerRowAttributeIndex() {
        return rowAttributeComboBox.getSelectedIndex();
    }

    public int gerColAttributeIndex() {
        return colAttributeComboBox.getSelectedIndex();
    }

    public double getAlpha() {
        return ((SpinnerNumberModel) alphaSpinner.getModel()).getNumber().doubleValue();
    }

    public boolean isUseYates() {
        return useYatesCheckbox.isSelected();
    }

    public boolean isDialogResult() {
        return dialogResult;
    }

    private void init() {
        JPanel attributesPanel = createAttributesPanel();
        JPanel chiSquaredTestPanel = createChiSquaredTestOptionsPanel();
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });
        okButton.addActionListener(event -> {
            try {
                validateSelectedAttributes();
                dialogResult = true;
                setVisible(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ContingencyTableOptionsDialog.this,
                        e.getMessage(), StringUtils.EMPTY, JOptionPane.WARNING_MESSAGE);
            }
        });

        this.add(attributesPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 10, 5), 0, 0));
        this.add(chiSquaredTestPanel, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 10, 5), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

    private void validateSelectedAttributes() {
        int rowAttrIndex = rowAttributeComboBox.getSelectedIndex();
        int colAttrIndex = colAttributeComboBox.getSelectedIndex();
        if (rowAttrIndex == colAttrIndex) {
            throw new IllegalArgumentException(DUPLICATE_ATTRIBUTES_ERROR_MESSAGE);
        } else if (!data.attribute(rowAttrIndex).isNominal()) {
            throw new IllegalArgumentException(
                    String.format(INVALID_ATTR_TYPE_ERROR_FORMAT, data.attribute(rowAttrIndex).name()));
        } else if (!data.attribute(colAttrIndex).isNominal()) {
            throw new IllegalArgumentException(
                    String.format(INVALID_ATTR_TYPE_ERROR_FORMAT, data.attribute(colAttrIndex).name()));
        }
    }

    private JPanel createAttributesPanel() {
        JPanel attributesPanel = new JPanel(new GridBagLayout());
        attributesPanel.setBorder(PanelBorderUtils.createTitledBorder(ATTRIBUTES_TEXT));
        String[] attributesNames = Utils.getAttributeNames(data);
        rowAttributeComboBox = new JComboBox<>(attributesNames);
        rowAttributeComboBox.setPreferredSize(COMBO_BOX_DIM);
        rowAttributeComboBox.setMinimumSize(COMBO_BOX_DIM);
        rowAttributeComboBox.setMinimumSize(COMBO_BOX_DIM);
        rowAttributeComboBox.setSelectedIndex(0);
        colAttributeComboBox = new JComboBox<>(attributesNames);
        colAttributeComboBox.setPreferredSize(COMBO_BOX_DIM);
        colAttributeComboBox.setMinimumSize(COMBO_BOX_DIM);
        colAttributeComboBox.setMinimumSize(COMBO_BOX_DIM);
        colAttributeComboBox.setSelectedIndex(1);
        attributesPanel.add(new JLabel(ROW_ATTR_TEXT),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        attributesPanel.add(rowAttributeComboBox, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        attributesPanel.add(new JLabel(COL_ATTR_TEXT), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        attributesPanel.add(colAttributeComboBox, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        return attributesPanel;
    }

    private JPanel createChiSquaredTestOptionsPanel() {
        JPanel chiSquaredTestPanel = new JPanel(new GridBagLayout());
        chiSquaredTestPanel.setBorder(PanelBorderUtils.createTitledBorder(CHI_SQUARE_OPTIONS_TEXT));
        alphaSpinner = new JSpinner();
        alphaSpinner.setModel(
                new SpinnerNumberModel(DEFAULT_ALPHA_VALUE, ALPHA_MIN_VALUE, MAX_ALPHA_VALUE, ALPHA_STEP));
        useYatesCheckbox = new JCheckBox(YATES_CORRECTION_TEXT);
        chiSquaredTestPanel.add(new JLabel(CHI_SQUARE_TEXT),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        chiSquaredTestPanel.add(alphaSpinner, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        chiSquaredTestPanel.add(useYatesCheckbox, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
        return chiSquaredTestPanel;
    }
}
