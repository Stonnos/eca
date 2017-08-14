package eca.core.converters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Implements saving image to file.
 *
 * @author Roman Batygin
 */
public class ImageSaver {

    /**
     * Saves image to file.
     *
     * @param file file object
     * @param img  image
     * @throws IOException
     */
    public static void saveImage(File file, Image img) throws IOException {
        if (file.getName().endsWith(".png")) {
            ImageIO.write((BufferedImage) img, "png", file);
        } else {
            throw new IOException("Wrong file extension!");
        }

    }
}
