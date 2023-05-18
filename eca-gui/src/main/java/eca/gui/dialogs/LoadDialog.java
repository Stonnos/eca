package eca.gui.dialogs;

import eca.gui.actions.CallbackAction;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * Implements loading process dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public class LoadDialog extends AbstractProgressDialog {

    private final CallbackAction action;

    public LoadDialog(Window parent, CallbackAction action, String loadingMessage) {
        this(parent, action, loadingMessage, true);
    }

    public LoadDialog(Window parent, CallbackAction action, String loadingMessage, boolean closable) {
        super(parent, loadingMessage, true, false, closable);
        this.action = action;
    }

    @Override
    protected AbstractBackgroundTask createBackgroundTask() {
        return new LoadingTask();
    }

    /**
     * Loading task implementation.
     */
    private class LoadingTask extends AbstractBackgroundTask {

        @Override
        void performTask() throws Exception {
            action.apply();
        }
    }
}
