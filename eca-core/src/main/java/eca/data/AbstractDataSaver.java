package eca.data;

import java.io.File;
import java.util.Objects;

/**
 * Abstract class for saving data.
 *
 * @author Roman Batygin
 */
public abstract class AbstractDataSaver implements DataSaver {

    private File file;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Sets file object.
     *
     * @param file {@link File} object
     */
    public void setFile(File file) {
        validateFile(file);
        this.file = file;
    }

    /**
     * Returns source file.
     *
     * @return source file
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns date format.
     *
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     *
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Objects.requireNonNull(dateFormat, "Date format is not specified!");
        this.dateFormat = dateFormat;
    }

    /**
     * Checks if file is valid.
     *
     * @param file - file object
     * @return {@code true} if file is valid
     */
    protected abstract boolean isValidFile(File file);

    private void validateFile(File file) {
        Objects.requireNonNull(file, "File is not specified!");
        if (!isValidFile(file)) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }
}
