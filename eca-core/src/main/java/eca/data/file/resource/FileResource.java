package eca.data.file.resource;

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
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return FileUtils.openInputStream(getResource());
    }

    @Override
    public String getFile() {
        return getResource().getName();
    }
}
