package eca.gui;

import eca.gui.actions.CallbackAction;
import eca.gui.dialogs.ExecutorDialog;

/**
 * @author Roman Batygin
 */

public class ExecutorService {

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
