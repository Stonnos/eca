package eca.gui.actions;

import lombok.Getter;

/**
 * @author Roman Batygin
 */
@Getter
public abstract class AbstractCallback<T> implements CallbackAction {

    protected T result;
}
