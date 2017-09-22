package eca.core.converters;

import org.springframework.util.Assert;

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
     * @param img  image object
     * @throws IOException
     */
    public static void saveImage(File file, Image img) throws IOException {
        Assert.notNull(file, "File is not specified!");
        Assert.notNull(img, "Image is not specified!");
        if (file.getName().endsWith(DataFileExtension.PNG)) {
            ImageIO.write((BufferedImage) img, "png", file);
        } else {
            throw new IOException(String.format("Can't save image to file '%s'", file.getAbsoluteFile()));
        }

    }
}
