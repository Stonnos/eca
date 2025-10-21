package eca.data.file.resource;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/**
 * Zip entry resource wrapper.
 *
 * @author Roman Batygin
 */
public class ZipEntryResource extends AbstractResource<ZipEntry> {

    private final InputStream inputStream;

    /**
     * Creates zip entry resource.
     *
     * @param zipEntry - zip entry
     * @param inputStream - input stream
     */
    public ZipEntryResource(ZipEntry zipEntry, InputStream inputStream) {
        super(zipEntry);
        this.inputStream = inputStream;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public String getFile() {
        return getResource().getName();
    }

    @Override
    public String getExtension() {
        return FilenameUtils.getExtension(getFile());
    }
}
