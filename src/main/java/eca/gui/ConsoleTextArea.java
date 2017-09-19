package eca.gui;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

/**
 * @author Roman Batygin
 */

public class ConsoleTextArea {

    private static final Font DEFAULT_TEXT_AREA_FONT = new Font("Arial", Font.BOLD, 12);

    private static final Color CONSOLE_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.WHITE;

    private static JTextArea textArea;

    private ConsoleTextArea() {

    }

    public static JTextArea getTextArea() {
        if (textArea == null) {
            textArea = new JTextArea(30, 85);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setFont(DEFAULT_TEXT_AREA_FONT);
            textArea.setBackground(CONSOLE_COLOR);
            textArea.setForeground(TEXT_COLOR);
        }
        return textArea;
    }
}
