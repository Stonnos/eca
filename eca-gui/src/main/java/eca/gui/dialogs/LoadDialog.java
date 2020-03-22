package eca.gui.dialogs;

import eca.gui.actions.CallbackAction;
import eca.gui.logging.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Implements loading process dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public class LoadDialog extends JDialog implements ExecutorDialog {

    private static final long DELAY = 300L;

    private final CallbackAction action;
    private final JProgressBar progress;
    private SwingWorkerConstruction worker;

    private boolean isSuccess = true;
    private String errorMessage;

    private final StopWatch stopWatch = new StopWatch();

    public LoadDialog(Window parent, CallbackAction action, String msg) {
        super(parent, StringUtils.EMPTY);
        this.setModal(true);
        this.action = action;
        this.setResizable(false);
        this.setLayout(new GridBagLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                if (!isCancelled()) {
                    worker.cancel(true);
                }
            }
        });

        progress = new JProgressBar();
        progress.setIndeterminate(true);
        this.add(new JLabel(msg),
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(10, 5, 10, 5), 0, 0));
        this.add(progress, new GridBagConstraints(0, 1, 3, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        this.pack();
        this.setLocationRelativeTo(parent);
        worker = new SwingWorkerConstruction();
    }

    @Override
    public void execute() {
        worker.execute();
        this.setVisible(true);
    }

    @Override
    public boolean isCancelled() {
        return worker.isCancelled();
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
     *
     */
    private class SwingWorkerConstruction extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            try {
                stopWatch.start();
                action.apply();
                stopWatch.stop();
            } catch (Exception e) {
                handleError(e);
            }
            setProgress(100);
            delay();
            return null;
        }

        void handleError(Exception e) {
            LoggerUtils.error(log, e);
            isSuccess = false;
            errorMessage = e.getMessage();
        }

        void delay() {
            if (!isCancelled()) {
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
