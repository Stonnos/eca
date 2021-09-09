package eca.data;

import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * File utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FileUtils {

    /**
     * Txt extensions
     */
    public static final Set<String> TXT_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.TEXT.getExtendedExtension(),
                    DataFileExtension.DATA.getExtendedExtension());

    /**
     * Xls extensions
     */
    public static final Set<String> XLS_EXTENSIONS =
            ImmutableSet.of(DataFileExtension.XLS.getExtendedExtension(),
                    DataFileExtension.XLSX.getExtendedExtension());

    /**
     * Docx extensions
     */
    public static final Set<String> DOCX_EXTENSIONS =
            Collections.singleton(DataFileExtension.DOCX.getExtendedExtension());

    /**
     * All extensions
     */
    public static final Set<String> ALL_EXTENSIONS = Stream.of(DataFileExtension.values())
            .map(DataFileExtension::getExtension)
            .collect(Collectors.toSet());

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

    /**
     * Checks that file belongs to extensions list.
     *
     * @param fileName   - file name
     * @param extensions - extensions list
     * @return {@code true} if file belongs to extensions list
     */
    public static boolean containsExtension(String fileName, Set<String> extensions) {
        return extensions.stream().anyMatch(fileName::endsWith);
    }

    /**
     * Checks that input has valid extension (one of of xls, xlsx, csv, arff, json, xml, txt, data, docx).
     *
     * @param fileName - train data file name
     * @return {@code true} if train data is valid
     */
    public static boolean isValidTrainDataFile(String fileName) {
        if (fileName != null) {
            String extension = FilenameUtils.getExtension(fileName);
            return ALL_EXTENSIONS.contains(extension);
        }
        return false;
    }
}
