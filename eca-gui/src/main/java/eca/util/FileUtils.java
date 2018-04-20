package eca.util;

/**
 * File utils class.
 *
 * @author Roman Batygin
 */
public class FileUtils {

    private static final String JAVA_CLASS_PATH = "java.class.path";
    private static final String FILE_SEPARATOR = "file.separator";

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

}
