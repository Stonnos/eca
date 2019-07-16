package eca.gui.actions;

import lombok.Getter;

/**
 * @author Roman Batygin
 */
@Getter
public abstract class AbstractCallback<T> implements CallbackAction {

    private T result;

    @Override
    public void apply() throws Exception {
        result = performAndGetResult();
    }

    protected abstract T performAndGetResult() throws Exception;
}
