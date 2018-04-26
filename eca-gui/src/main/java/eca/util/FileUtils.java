package eca.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * File utils class.
 *
 * @author Roman Batygin
 */
public class FileUtils {

    private static final String JAVA_CLASS_PATH = "java.class.path";
    private static final String FILE_SEPARATOR = "file.separator";
    private static final String PNG = "png";

    /**
     * Returns project current directory (target directory).
     *
     * @return project current directory
     */
    public static String getCurrentDir() {
        String path = System.getProperty(JAVA_CLASS_PATH);
        String fileSeparator = System.getProperty(FILE_SEPARATOR);
        return path.substring(0, path.lastIndexOf(fileSeparator) + 1);
    }

    /**
     * Saves image to file.
     *
     * @param file file object
     * @param img  image object
     * @throws IOException
     */
    public static void write(File file, Image img) throws IOException {
        Objects.requireNonNull(file, "File is not specified!");
        Objects.requireNonNull(img, "Image is not specified!");
        if (!file.getName().endsWith(String.format(".%s", PNG))) {
            throw new IllegalArgumentException(String.format("Unexpected file extension '%s'", file.getAbsoluteFile()));
        } else {
            ImageIO.write((BufferedImage) img, PNG, file);
        }

    }

}
