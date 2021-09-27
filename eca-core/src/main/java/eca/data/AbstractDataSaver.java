package eca.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import weka.core.Instances;

import java.io.File;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract class for saving data.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractDataSaver implements DataSaver {

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Supported extensions
     */
    @Getter
    private final Set<String> supportedExtensions;

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

    @Override
    public void write(Instances data, File file) throws Exception {
        Objects.requireNonNull(data, "Instances ins't specified!");
        validateFile(file);
        internalWrite(data, file);
    }

    protected abstract void internalWrite(Instances data, File file) throws Exception;

    private boolean isValidExtension(File file) {
        return FileUtils.containsExtension(file.getName(), supportedExtensions);
    }

    private void validateFile(File file) {
        Objects.requireNonNull(file, "File is not specified!");
        if (!isValidExtension(file)) {
            throw new IllegalArgumentException(
                    String.format("Unexpected extension for file: %s! Expected one of %s", file.getName(),
                            supportedExtensions));
        }
    }
}
