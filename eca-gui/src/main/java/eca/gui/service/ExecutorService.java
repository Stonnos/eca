package eca.gui.service;

import eca.gui.actions.CallbackAction;
import eca.gui.dialogs.ExecutorDialog;
import lombok.experimental.UtilityClass;

/**
 * Service for execution asynchronous task.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ExecutorService {

    /**
     * Executes asynchronous task.
     *
     * @param progress      <tt>ExecutorDialog</tt> object.
     * @param successAction callback function for success execution
     * @param failAction    callback function for failed execution
     * @throws Exception in case of error
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
