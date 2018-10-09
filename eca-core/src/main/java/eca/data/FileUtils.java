package eca.data;

import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Set;

/**
 * File utility class.
 */
public class FileUtils {

    /**
     * Weka extensions
     */
    private static final Set<String> WEKA_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.CSV.getExtendedExtension(), DataFileExtension.ARFF.getExtendedExtension(),
                    DataFileExtension.JSON.getExtendedExtension());

    /**
     * Txt extensions
     */
    private static final Set<String> TXT_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.TEXT.getExtendedExtension(),
                    DataFileExtension.DATA.getExtendedExtension());

    /**
     * Xls extensions
     */
    private static final Set<String> XLS_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.XLS.getExtendedExtension(),
                    DataFileExtension.XLSX.getExtendedExtension());

    /**
     * Docx extensions
     */
    private static final Set<String> DOCX_EXTENSIONS =
            Collections.singleton(DataFileExtension.DOCX.getExtendedExtension());

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
     * Returns true if specified file extension belongs to txt formats (txt, data).
     *
     * @param fileName - file name
     * @return true if specified file extension belongs to txt formats (txt, data)
     */
    public static boolean isTxtExtension(String fileName) {
        return containsExtension(fileName, TXT_EXTENSIONS);
    }

    /**
     * Returns true if specified file extension belongs to docx format.
     *
     * @param fileName - file name
     * @return true if specified file extension belongs to docx format
     */
    public static boolean isDocxExtension(String fileName) {
        return containsExtension(fileName, DOCX_EXTENSIONS);
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
