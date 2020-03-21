package eca.gui.font;

import java.awt.*;
import java.util.stream.IntStream;

/**
 * Font manager class.
 *
 * @author Roman Batygin
 */
public class FontManager {

    private static final int DEFAULT_FONT_SIZE = 12;

    private static FontManager fontManager;

    private String[] availableFontNames;
    private Font[] allFonts;

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
        return getFontNames();
    }

    /**
     * Gets all fonts array.
     *
     * @return all fonts array
     */
    public synchronized Font[] getAllFonts() {
        if (allFonts == null) {
            String[] fontNames = getFontNames();
            allFonts = new Font[fontNames.length];
            IntStream.range(0, allFonts.length).forEach(
                    i -> allFonts[i] = new Font(fontNames[i], Font.PLAIN, DEFAULT_FONT_SIZE));
        }
        return allFonts;
    }

    private String[] getFontNames() {
        if (availableFontNames == null) {
            availableFontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        }
        return availableFontNames;
    }
}
