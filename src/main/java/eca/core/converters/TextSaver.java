package eca.core.converters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Roman Batygin
 */

public class TextSaver {

    public static void saveToFile(File file, String text) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "Cp1251"))) {
            writer.write(text);
        }
    }
}
