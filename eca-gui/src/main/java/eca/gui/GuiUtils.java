package eca.gui;

import eca.gui.logging.LoggerUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;

/**
 * GUI utility class.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class GuiUtils {

    private static final String INPUT_ERROR_TEXT = "Ошибка ввода";
    private static final String FILL_ALL_FIELDS_ERROR_TEXT = "Заполните все поля!";

    public static JTextField searchFirstEmptyField(JTextField... fields) {
        if (fields != null) {
            for (JTextField field : fields) {
                if (isEmpty(field)) {
                    return field;
                }
            }
        }
        return null;
    }

    public static boolean isEmpty(JTextField jTextField) {
        return !Optional.ofNullable(jTextField).map(JTextField::getText).isPresent()
                || StringUtils.isEmpty(jTextField.getText().trim());
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

    public static void showErrorMessageAndRequestFocusOn(Window component, JComponent target) {
        JOptionPane.showMessageDialog(component,
                FILL_ALL_FIELDS_ERROR_TEXT,
                INPUT_ERROR_TEXT, JOptionPane.WARNING_MESSAGE);
        target.requestFocusInWindow();
    }

    public static void setIcon(Window window, URL iconUrl) {
        try {
            window.setIconImage(ImageIO.read(iconUrl));
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
        }
    }
}
