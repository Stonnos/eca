package eca.data.file.resource;

import eca.data.DataFileExtension;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * File resource wrapper.
 *
 * @author Roman Batygin
 */
public class FileResource extends AbstractResource<File> {

    /**
     * Creates file resource with specified file.
     *
     * @param file - file
     */
    public FileResource(File file) {
        super(file);
        if (!file.getName().endsWith(DataFileExtension.XLS.getExtension()) &&
                !file.getName().endsWith(DataFileExtension.XLSX.getExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", file.getName()));
        }
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return FileUtils.openInputStream(getResource());
    }
}
