package eca.data;

/**
 * File utility class.
 */
public class FileUtil {

    /**
     * Returns true if specified file extension belongs to weka formats (csv, arff, json).
     *
     * @param fileName - file name
     * @return true if specified file extension belongs to weka formats (csv, arff, json)
     */
    public static boolean isWekaExtension(String fileName) {
        return fileName.endsWith(DataFileExtension.CSV.getExtension()) ||
                fileName.endsWith(DataFileExtension.ARFF.getExtension()) ||
                fileName.endsWith(DataFileExtension.JSON.getExtension());
    }

    /**
     * Returns true if specified file extension belongs to xls formats (xls, xlsx).
     *
     * @param fileName - file name
     * @return true if specified file extension belongs to xls formats (xls, xlsx)
     */
    public static boolean isXlsExtension(String fileName) {
        return fileName.endsWith(DataFileExtension.XLS.getExtension()) ||
                fileName.endsWith(DataFileExtension.XLSX.getExtension());
    }
}
