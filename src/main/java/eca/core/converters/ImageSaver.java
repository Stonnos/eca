package eca.core.converters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Roman Batygin
 */

public class ImageSaver {

    public static void saveImage(File file, Image img) throws IOException {
        if (file.getName().endsWith(".png")) {
            ImageIO.write((BufferedImage) img, "png", file);
        } else {
            throw new IOException("Wrong file extension!");
        }

    }
}
