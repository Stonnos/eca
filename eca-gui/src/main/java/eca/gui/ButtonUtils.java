package eca.gui;

import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */
@UtilityClass
public class ButtonUtils {

    private static final String OK_BUTTON_TEXT = "ОК";

    private static final String CANCEL_BUTTON_TEXT = "Отмена";

    private static final String CLOSE_BUTTON = "Закрыть";

    private static final int BUTTON_WIDTH = 85;

    private static final int BUTTON_HEIGHT = 25;
    public static final Dimension DEFAULT_DIMENSION = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);

    public static JButton createOkButton() {
        return createButton(OK_BUTTON_TEXT, DEFAULT_DIMENSION);
    }

    public static JButton createCancelButton() {
        return createButton(CANCEL_BUTTON_TEXT, DEFAULT_DIMENSION);
    }

    public static JButton createCloseButton() {
        JButton button = new JButton(CLOSE_BUTTON);
        Dimension dimension = new Dimension(100, 25);
        button.setPreferredSize(dimension);
        button.setMinimumSize(dimension);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        return button;
    }

    public static JButton createButton(String title) {
        return createButton(title, null);
    }

    public static JButton createButton(String title, Dimension dimension) {
        JButton button = new JButton(title);
        if (dimension != null) {
            button.setPreferredSize(dimension);
            button.setMinimumSize(dimension);
        }
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        return button;
    }

}
