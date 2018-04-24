package eca.data.file.resource;

import java.util.Objects;

/**
 * Abstract resource wrapper.
 *
 * @author Roman Batygin
 */
public abstract class AbstractResource<S> implements DataResource<S> {

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
