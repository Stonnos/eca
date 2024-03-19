package eca.gui.dialogs;

import eca.ensemble.IterativeBuilder;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * Implements classifier iterative building dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ClassifierBuilderDialog extends AbstractProgressDialog {

    private final IterativeBuilder builder;

    public ClassifierBuilderDialog(Window parent, IterativeBuilder builder, String loadingMessage) {
        super(parent, loadingMessage, false, true, true);
        this.builder = builder;
    }

    @Override
    protected AbstractBackgroundTask createBackgroundTask() {
        return new ClassifierBuilderTask();
    }

    /**
     * Classifier builder task.
     */
    private class ClassifierBuilderTask extends AbstractBackgroundTask {

        @Override
        void performTask() throws Exception {
            while (!super.isCancelled() && builder.hasNext()) {
                builder.next();
                setProgress(builder.getPercent());
            }
            if (!super.isCancelled()) {
                builder.evaluation().setTotalTimeMillis(getTotalTimeMillis());
            }
        }
    }
}
