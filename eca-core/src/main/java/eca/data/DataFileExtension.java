package eca.data;

/**
 * File extension for input data.
 *
 * @author Roman Batygin
 */

public enum DataFileExtension {

    XLS(".xls"),

    XLSX(".xlsx"),

    ARFF(".arff"),

    CSV(".csv"),

    JSON(".json");

    private String extension;

    DataFileExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Returns extension in format: .ext
     *
     * @return extension in format: .ext
     */
    public String getExtension() {
        return extension;
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
