package eca.io;

/**
 * @author Roman Batygin
 */

public class FileUtils {

    public static String getCurrentDir() {
        String path = System.getProperty("java.class.path");
        String fileSeparator = System.getProperty("file.separator");
        return path.substring(0, path.lastIndexOf(fileSeparator) + 1);
    }

}
