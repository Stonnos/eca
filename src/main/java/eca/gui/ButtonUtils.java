package eca.gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */

public class ButtonUtils {

    private static final String OK_BUTTON_TEXT = "OK";

    private static final String CANCEL_BUTTON_TEXT = "Cancel";

    private static final String CLOSE_BUTTON = "Закрыть";

    private static final int BUTTON_WIDTH = 85;

    private static final int BUTTON_HEIGHT = 25;

    public static Dimension getButtonDimension() {
        return new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    public static JButton createOkButton() {
        return createButton(OK_BUTTON_TEXT);
    }

    public static JButton createCancelButton() {
        return createButton(CANCEL_BUTTON_TEXT);
    }

    public static JButton createCloseButton() {
        JButton button = new JButton(CLOSE_BUTTON);
        Dimension dimension = new Dimension(100, 25);
        button.setPreferredSize(dimension);
        button.setMinimumSize(dimension);
        return button;
    }

    public static JButton createButton(String title) {
        JButton button = new JButton(title);
        Dimension dimension = getButtonDimension();
        button.setPreferredSize(dimension);
        button.setMinimumSize(dimension);
        return button;
    }

}
