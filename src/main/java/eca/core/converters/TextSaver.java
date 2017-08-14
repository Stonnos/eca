package eca.core.converters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Implemets saving text to file.
 *
 * @author Roman Batygin
 */
public class TextSaver {

    /**
     * Saves text to file.
     *
     * @param file file object
     * @param text text object
     * @throws IOException
     */
    public static void saveToFile(File file, String text) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "Cp1251"))) {
            writer.write(text);
        }
    }
}
