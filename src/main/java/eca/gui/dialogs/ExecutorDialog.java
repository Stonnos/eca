package eca.gui.dialogs;

/**
 * @author Roman Batygin
 */
public interface ExecutorDialog {

    void execute();

    boolean isCancelled();

    boolean isSuccess();

    String getErrorMessageText();
}
