package eca.gui.service;

import eca.gui.actions.CallbackAction;
import eca.gui.dialogs.ExecutorDialog;
import eca.gui.frames.results.ClassificationResultsFrameBase;
import lombok.experimental.UtilityClass;

import java.awt.*;

import static eca.gui.GuiUtils.showFormattedErrorMessageDialog;

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
     * @param progress      - ExecutorDialog object.
     * @param successAction - callback function for success execution
     * @param failAction    - callback function for failed execution
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
        progress.clear();
    }

    /**
     * Executes asynchronous task with empty success callback and default error callback.
     *
     * @param progress - ExecutorDialog object
     * @param parent   - parent component
     * @throws Exception in case of error
     */
    public static void process(ExecutorDialog progress, Component parent) throws Exception {
        process(progress, () -> {
        }, () -> showFormattedErrorMessageDialog(parent, progress.getErrorMessageText()));
    }
}
