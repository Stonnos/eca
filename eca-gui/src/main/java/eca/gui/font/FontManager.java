package eca.gui.font;

import java.awt.GraphicsEnvironment;

/**
 * Font manager class.
 *
 * @author Roman Batygin
 */
public class FontManager {

    private static FontManager fontManager;

    private String[] availableFontNames;

    private FontManager() {
    }

    /**
     * Creates font manager instance.
     *
     * @return font manager instance
     */
    public static synchronized FontManager getFontManager() {
        if (fontManager == null) {
            fontManager = new FontManager();
        }
        return fontManager;
    }

    /**
     * Gets available font names array.
     *
     * @return font names array
     */
    public synchronized String[] getAvailableFontNames() {
        if (availableFontNames == null) {
            availableFontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        }
        return availableFontNames;
    }
}
