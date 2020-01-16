package eca.util;

import lombok.experimental.UtilityClass;

import java.awt.*;

/**
 * Font utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FontUtils {

    /**
     * Calculates x coordinate for string message in rectangle.
     *
     * @param value - string message
     * @param fm    - font metrics
     * @param x1    - x1 rectangle coordinate (upper left corner coordinate)
     * @param width - rectangle width
     * @return calculated x value
     */
    public static float calculateXForString(String value, FontMetrics fm, float x1, float width) {
        return x1 + (width - fm.stringWidth(value)) / 2.0f;
    }

    /**
     * Calculates x coordinate for string message in rectangle.
     *
     * @param fm     - font metrics
     * @param y1     - y1 rectangle coordinate (upper left corner coordinate)
     * @param height - rectangle height
     * @return calculated x value
     */
    public static float calculateYForString(FontMetrics fm, float y1, float height) {
        return y1 + fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2.0f;
    }
}
