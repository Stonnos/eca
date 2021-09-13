package eca.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Text area singleton for console.
 *
 * @author Roman Batygin
 */
public class ConsoleTextArea {

    private static final Font DEFAULT_TEXT_AREA_FONT = new Font("Arial", Font.BOLD, 12);

    private static final Color CONSOLE_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int ROWS = 30;
    private static final int COLUMNS = 85;
    private static final Color SELECTION_COLOR = Color.YELLOW;

    private static JTextArea textArea;

    private ConsoleTextArea() {
    }

    /**
     * Creates text area for console.
     *
     * @return text area
     */
    public static synchronized JTextArea getTextArea() {
        if (textArea == null) {
            textArea = new JTextArea(ROWS, COLUMNS);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setFont(DEFAULT_TEXT_AREA_FONT);
            textArea.setBackground(CONSOLE_COLOR);
            textArea.setForeground(TEXT_COLOR);
            textArea.setSelectionColor(SELECTION_COLOR);
        }
        return textArea;
    }
}
