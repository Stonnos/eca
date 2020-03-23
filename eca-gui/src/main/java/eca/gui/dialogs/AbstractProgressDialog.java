package eca.gui.dialogs;

import eca.gui.logging.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

/**
 * Abstract progress dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public abstract class AbstractProgressDialog extends JDialog implements ExecutorDialog {

    private static final long DELAY = 300L;
    private static final int FULL_PROGRESS = 100;

    private AbstractBackgroundTask backgroundTask;

    private boolean isSuccess = true;
    private String errorMessage;

    private final StopWatch stopWatch = new StopWatch();

    public AbstractProgressDialog(Window parent, String loadingMessage, boolean intermediate) {
        super(parent, StringUtils.EMPTY);
        this.setModal(true);
        this.setResizable(false);
        this.createGUI(loadingMessage, intermediate);
        this.addCancelListener();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void execute() {
        backgroundTask = createBackgroundTask();
        backgroundTask.execute();
        this.setVisible(true);
    }

    @Override
    public boolean isCancelled() {
        return Optional.ofNullable(backgroundTask).map(AbstractBackgroundTask::isCancelled).orElse(false);
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public String getErrorMessageText() {
        return errorMessage;
    }

    public long getTotalTimeMillis() {
        return stopWatch.getTime();
    }

    /**
     * Creates background task object.
     * @return background task object
     */
    protected abstract AbstractBackgroundTask createBackgroundTask();

    private void createGUI(String loadingMessage, boolean intermediate) {
        this.setLayout(new GridBagLayout());
        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(intermediate);
        this.add(new JLabel(loadingMessage),
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(10, 5, 10, 5), 0, 0));
        this.add(progress, new GridBagConstraints(0, 1, 3, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    }

    private void addCancelListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                if (!isCancelled()) {
                    backgroundTask.cancel(true);
                }
            }
        });
    }

    /**
     * Abstract background task class.
     */
    protected abstract class AbstractBackgroundTask extends SwingWorker<Void, Void> {

        abstract void performTask();

        @Override
        protected Void doInBackground() {
            try {
                stopWatch.start();
                performTask();
                stopWatch.stop();
            } catch (Exception e) {
                handleError(e);
            }
            setProgress(FULL_PROGRESS);
            delay();
            return null;
        }

        void handleError(Exception e) {
            LoggerUtils.error(log, e);
            isSuccess = false;
            errorMessage = e.getMessage();
        }

        void delay() {
            if (!super.isCancelled()) {
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        @Override
        protected void done() {
            setVisible(false);
        }
    }
}
