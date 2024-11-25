package eca.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Reference wrapper.
 *
 * @param <T> - item type
 * @author Roman Batygin
 */
@Getter
@AllArgsConstructor
public class ReferenceWrapper<T> {

    private T item;

    /**
     * Clear wrapped item reference.
     */
    public void clear() {
        item = null;
    }
}
