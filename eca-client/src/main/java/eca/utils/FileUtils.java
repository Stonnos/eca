package eca.utils;

/**
 * File utils class.
 *
 * @author Roman Batygin
 */

public class FileUtils {

    /**
     * Returns project current directory (target directory).
     *
     * @return project current directory
     */
    public static String getCurrentDir() {
        String path = System.getProperty("java.class.path");
        String fileSeparator = System.getProperty("file.separator");
        return path.substring(0, path.lastIndexOf(fileSeparator) + 1);
    }

}
