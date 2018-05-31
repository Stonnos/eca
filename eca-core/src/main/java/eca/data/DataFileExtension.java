package eca.data;

/**
 * File extension for input data.
 *
 * @author Roman Batygin
 */
public enum DataFileExtension {

    /**
     * Xls file extension
     */
    XLS("xls", "Xls data files (*.xls)"),

    /**
     * Xlsx file extension
     */
    XLSX("xlsx", "Xlsx data files (*.xlsx)"),

    /**
     * ARFF file extension
     */
    ARFF("arff", "Arff data files (*.arff)"),

    /**
     * Csv file extension
     */
    CSV("csv", "Csv data files (*.csv)"),

    /**
     * Json file extension
     */
    JSON("json", "Json data files (*.json)"),

    /**
     * Text file extension
     */
    TEXT("txt", "Text data files (*.txt)"),

    /**
     * Data file extension
     */
    DATA("data", "DATA data files (*.data)"),

    /**
     * Docx file extension
     */
    DOCX("docx", "Docx data files (*.docx)");

    /**
     * File extension
     */
    private String extension;

    /**
     * Extension description
     */
    private String description;

    DataFileExtension(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    /**
     * Returns extension in format: ext
     *
     * @return extension in format: ext
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Returns extension description.
     *
     * @return extension description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns files extensions.
     *
     * @return files extensions
     */
    public static String[] getExtensions() {
        DataFileExtension[] values = values();
        String[] extensions = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            extensions[i] = values[i].getExtension();
        }
        return extensions;
    }
}
