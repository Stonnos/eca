package eca.data.file.resource;

import java.io.InputStream;

/**
 * Input stream resource wrapper.
 *
 * @author Roman Batygin
 */
public class InputStreamResource extends AbstractResource<InputStream> {

    /**
     * Creates input stream resource.
     *
     * @param inputStream - input stream
     */
    public InputStreamResource(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public InputStream openInputStream() {
        return getResource();
    }
}
