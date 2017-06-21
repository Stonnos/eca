package eca.gui;

import javax.swing.JTextField;

/**
 * @author Roman Batygin
 */

public class GuiUtils {

    public static JTextField searchFirstEmptyField(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                return field;
            }
        }
        return null;
    }
}
