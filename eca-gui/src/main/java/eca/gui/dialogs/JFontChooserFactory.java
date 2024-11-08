package eca.gui.dialogs;

import lombok.experimental.UtilityClass;

import java.awt.*;

/**
 * Font chooser factory class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class JFontChooserFactory {

    /**
     * Gets selected font or default value.
     *
     * @param parent      - parent window
     * @param defaultFont - default font
     * @return selected font or default value
     */
    public static Font getSelectedFontOrDefault(Window parent, Font defaultFont) {
        JFontChooser fontChooser = new JFontChooser(parent, defaultFont);
        fontChooser.setVisible(true);
        Font resultFont = fontChooser.dialogResult() ? fontChooser.getSelectedFont() : defaultFont;
        fontChooser.dispose();
        return resultFont;
    }
}
