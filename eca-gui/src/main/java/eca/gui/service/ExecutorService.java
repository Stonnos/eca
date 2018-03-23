package eca.gui.service;

import eca.gui.actions.CallbackAction;
import eca.gui.dialogs.ExecutorDialog;

/**
 * Service for execution asynchronous task.
 *
 * @author Roman Batygin
 */
public class ExecutorService {

    /**
     * Executes asynchronous task.
     *
     * @param progress      <tt>ExecutorDialog</tt> object.
     * @param successAction callback function for success execution
     * @param failAction    callback function for failed execution
     * @throws Exception
     */
    public static void process(ExecutorDialog progress,
                               CallbackAction successAction,
                               CallbackAction failAction) throws Exception {
        progress.execute();
        if (!progress.isCancelled()) {
            if (progress.isSuccess()) {
                successAction.apply();
            } else {
                failAction.apply();
            }
        }
    }
}
