package eca.gui.dialogs;

import eca.ensemble.IterativeBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * Implements classifier iterative building dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ClassifierBuilderDialog extends AbstractProgressDialog {

    private IterativeBuilder builder;

    public ClassifierBuilderDialog(Window parent, IterativeBuilder builder, String loadingMessage) {
        super(parent, loadingMessage, false, true, true);
        this.builder = builder;
    }

    @Override
    protected AbstractBackgroundTask createBackgroundTask() {
        return new ClassifierBuilderTask(builder);
    }

    @Override
    public void clear() {
        builder = null;
        super.clear();
    }

    /**
     * Classifier builder task.
     */
    @RequiredArgsConstructor
    private class ClassifierBuilderTask extends AbstractBackgroundTask {

        private final IterativeBuilder iterativeBuilder;

        @Override
        void performTask() throws Exception {
            while (!super.isCancelled() && iterativeBuilder.hasNext()) {
                iterativeBuilder.next();
                setProgress(iterativeBuilder.getPercent());
            }
            if (!super.isCancelled()) {
                iterativeBuilder.evaluation().setTotalTimeMillis(getTotalTimeMillis());
            }
        }
    }
}
