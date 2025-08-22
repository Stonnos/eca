package eca.gui.dialogs;

import eca.gui.actions.CallbackAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * Implements loading process dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public class LoadDialog extends AbstractProgressDialog {

    private CallbackAction action;

    public LoadDialog(Window parent, CallbackAction action, String loadingMessage) {
        this(parent, action, loadingMessage, true);
    }

    public LoadDialog(Window parent, CallbackAction action, String loadingMessage, boolean closable) {
        this(parent, action, loadingMessage,closable, false);
    }

    public LoadDialog(Window parent, CallbackAction action, String loadingMessage,
                      boolean closable, boolean runInBackgroundEnabled) {
        super(parent, loadingMessage, true, false, closable, runInBackgroundEnabled);
        this.action = action;
    }

    @Override
    protected AbstractBackgroundTask createBackgroundTask() {
        return new LoadingTask(action);
    }

    @Override
    public void clear() {
        action = null;
        super.clear();
    }

    /**
     * Loading task implementation.
     */
    @RequiredArgsConstructor
    private class LoadingTask extends AbstractBackgroundTask {

        private final CallbackAction callbackAction;

        @Override
        void performTask() throws Exception {
            callbackAction.apply();
        }
    }
}
