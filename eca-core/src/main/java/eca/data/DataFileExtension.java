package eca.data;

import eca.core.DescriptiveEnum;

import java.util.stream.Stream;

/**
 * File extension for input data.
 *
 * @author Roman Batygin
 */
public enum DataFileExtension implements DescriptiveEnum {

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
     * Xml extension
     */
    XML("xml", "Xml data files (*.xml)");

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
     * Returns extension in format: .ext
     *
     * @return extension in format: .ext
     */
    public String getExtendedExtension() {
        return String.format(".%s", extension);
    }

    /**
     * Returns extension description.
     *
     * @return extension description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns files extensions.
     *
     * @return files extensions
     */
    public static String[] getExtensions() {
        return Stream.of(values()).map(DataFileExtension::getExtension).toArray(String[]::new);
    }
}
