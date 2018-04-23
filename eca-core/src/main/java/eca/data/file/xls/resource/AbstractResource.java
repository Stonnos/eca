package eca.data.file.xls.resource;

import java.util.Objects;

/**
 * Abstract resource wrapper.
 *
 * @author Roman Batygin
 */
public abstract class AbstractResource<S> implements Resource<S> {

    private final S resource;

    /**
     * Creates abstract resource object.
     *
     * @param resource - resource
     */
    protected AbstractResource(S resource) {
        Objects.requireNonNull(resource, "Resource isn't specified!");
        this.resource = resource;
    }

    @Override
    public S getResource() {
        return resource;
    }
}
