package eca.data.file.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resource interface.
 *
 * @author Roman Batygin
 */
public interface DataResource<S> {

    /**
     * Returns resource object.
     *
     * @return resource object
     */
    S getResource();

    /**
     * Opens and returns input stream.
     *
     * @return input stream
     */
    InputStream openInputStream() throws IOException;

    /**
     * Returns resource file name.
     *
     * @return resource file name
     */
    String getFile();

    /**
     * Returns file extension.
     *
     * @return file extension
     */
    String getExtension();
}
