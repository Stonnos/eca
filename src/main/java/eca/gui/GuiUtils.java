package eca.gui;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

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

    public static void setUIFont(FontUIResource f){
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
