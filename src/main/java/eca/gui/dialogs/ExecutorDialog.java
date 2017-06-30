package eca.gui.dialogs;

/**
 * Class for execution asynchronous task.
 * @author Roman Batygin
 */
public interface ExecutorDialog {

    /**
     * Executes task.
     */
    void execute();

    /**
     * Returns <tt>true</tt> if the task was cancelled.
     * @return <tt>true</tt> if the task was cancelled
     */
    boolean isCancelled();

    /**
     * Returns <tt>true</tt> if the task was successfully completed.
     * @return <tt>true</tt> if the task was successfully completed
     */
    boolean isSuccess();

    /**
     * Returns error message text.
     * @return error message text
     */
    String getErrorMessageText();

}
