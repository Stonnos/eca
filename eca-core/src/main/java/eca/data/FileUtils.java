package eca.data;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * File utility class.
 */
public class FileUtils {

    /**
     * Weka extensions
     */
    private static final Set<String> WEKA_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.CSV.getExtension(), DataFileExtension.ARFF.getExtension(),
                    DataFileExtension.JSON.getExtension());

    /**
     * Txt extensions
     */
    private static final Set<String> TXT_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.TEXT.getExtension(), DataFileExtension.DATA.getExtension());

    /**
     * Xls extensions
     */
    private static final Set<String> XLS_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.XLS.getExtension(), DataFileExtension.XLSX.getExtension());

    /**
     * Returns true if specified file extension belongs to weka formats (csv, arff, json).
     *
     * @param fileName - file name
     * @return true if specified file extension belongs to weka formats (csv, arff, json)
     */
    public static boolean isWekaExtension(String fileName) {
        return containsExtension(fileName, WEKA_EXTENSIONS);
    }

    /**
     * Returns true if specified file extension belongs to xls formats (xls, xlsx).
     *
     * @param fileName - file name
     * @return true if specified file extension belongs to xls formats (xls, xlsx)
     */
    public static boolean isXlsExtension(String fileName) {
        return containsExtension(fileName, XLS_EXTENSIONS);
    }

    /**
     * Returns true if specified file extension belongs to txt formats (txt, data, doc, docx).
     *
     * @param fileName - file name
     * @return true if specified file extension belongs to txt formats (txt, data, doc, docx)
     */
    public static boolean isTxtExtension(String fileName) {
        return containsExtension(fileName, TXT_EXTENSIONS);
    }

    private static boolean containsExtension(String fileName, Set<String> extensions) {
        for (String extension : extensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
