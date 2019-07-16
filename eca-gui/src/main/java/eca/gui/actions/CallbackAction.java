package eca.gui.actions;

/**
 * Callback action interface.
 *
 * @author Roman Batygin
 */
@FunctionalInterface
public interface CallbackAction {

    void apply() throws Exception;
}
