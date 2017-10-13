package eca.gui;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.util.Enumeration;

/**
 * @author Roman Batygin
 */

public class GuiUtils {

    public static final String INPUT_ERROR_TEXT = "Ошибка ввода";
    public static final String FILL_ALL_FIELDS_ERROR_TEXT = "Заполните все поля!";

    public static JTextField searchFirstEmptyField(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                return field;
            }
        }
        return null;
    }

    public static String searchSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> enumeration = buttonGroup.getElements(); enumeration.hasMoreElements(); ) {
            AbstractButton abstractButton = enumeration.nextElement();
            if (abstractButton.isSelected()) {
                return abstractButton.getText();
            }
        }
        return null;
    }

    public static void updateForegroundAndBackGround(JComponent target, JTable source, boolean isSelected) {
        if (isSelected) {
            target.setForeground(source.getSelectionForeground());
            target.setBackground(source.getSelectionBackground());
        } else {
            target.setForeground(source.getForeground());
            target.setBackground(source.getBackground());
        }
    }

    public static void showErrorMessageAndRequestFocusOn(JDialog component, JComponent target) {
        JOptionPane.showMessageDialog(component,
                FILL_ALL_FIELDS_ERROR_TEXT,
                INPUT_ERROR_TEXT, JOptionPane.WARNING_MESSAGE);
        target.requestFocusInWindow();
    }

    public static void setUIFont(FontUIResource f) {
        java.util.Enumeration keys = UIManager.getLookAndFeelDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

}
