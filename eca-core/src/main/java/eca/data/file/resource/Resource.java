package eca.data.file.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resource interface.
 *
 * @author Roman Batygin
 */
public interface Resource<S> {

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
}
