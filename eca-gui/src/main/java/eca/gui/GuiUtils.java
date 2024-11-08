package eca.gui;

import eca.client.dto.EcaResponse;
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

import static eca.gui.service.TemplateService.getErrorMessageAsHtml;
import static eca.gui.service.TemplateService.getValidationErrorsMessageAsHtml;

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
    private static final int MAX_ERROR_MESSAGE_LENGTH = 1024;

    /**
     * Recursively removes all components from specified container.
     *
     * @param container - container object
     */
    public static void removeComponents(Container container) {
        Component[] components = container.getComponents();
        for (Component comp : components) {
            if (comp != null) {
                if (comp instanceof Container) {
                    removeComponents((Container) comp);
                }
                if (comp instanceof Cleanable cleanable) {
                    cleanable.clear();
                }
                container.remove(comp);
            }
        }
    }

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

    public static int getScreenWidth() {
        GraphicsDevice screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return screenDevice.getDisplayMode().getWidth();
    }

    public static int getScreenHeight() {
        GraphicsDevice screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return screenDevice.getDisplayMode().getHeight();
    }

    public static void showFormattedErrorMessageDialog(Component parent, String message) {
        String truncatedMessage = StringUtils.substring(message, 0, MAX_ERROR_MESSAGE_LENGTH);
        String body = getErrorMessageAsHtml(truncatedMessage);
        JOptionPane.showMessageDialog(parent, new JLabel(body), null, JOptionPane.WARNING_MESSAGE);
    }

    public static void showValidationErrorsDialog(Component parent, EcaResponse ecaResponse) {
        String body = getValidationErrorsMessageAsHtml(ecaResponse);
        JOptionPane.showMessageDialog(parent, new JLabel(body), null, JOptionPane.WARNING_MESSAGE);
    }
}
