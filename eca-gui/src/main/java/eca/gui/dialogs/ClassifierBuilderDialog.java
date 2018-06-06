/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.core.evaluation.Evaluation;
import eca.ensemble.IterativeBuilder;
import eca.gui.logging.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Implements classifier iterative building dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ClassifierBuilderDialog extends JDialog implements ExecutorDialog {

    private static final long DELAY = 300L;

    private final IterativeBuilder builder;
    private final JProgressBar progress;
    private SwingWorkerConstruction worker;

    private boolean isSuccess = true;
    private String errorMessage;

    public ClassifierBuilderDialog(Window parent, IterativeBuilder builder, String msg) {
        super(parent, StringUtils.EMPTY);
        this.setModal(true);
        this.builder = builder;
        this.setResizable(false);
        this.setLayout(new GridBagLayout());
        //---------------------------------------------------
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                if (!isCancelled()) {
                    worker.cancel(true);
                }
            }
        });
        //----------------------------------------------------
        progress = new JProgressBar();
        progress.setStringPainted(true);
        this.add(new JLabel(msg),
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(10, 5, 10, 5), 0, 0));
        this.add(progress, new GridBagConstraints(0, 1, 3, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
        //---------------------------------------------------
        this.pack();
        this.setLocationRelativeTo(parent);
        worker = new SwingWorkerConstruction();
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public String getErrorMessageText() {
        return errorMessage;
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

    /**
     *
     */
    private class SwingWorkerConstruction extends SwingWorker<Void, Void> {

        SwingWorkerConstruction() {
            this.addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    progress.setValue((Integer) evt.getNewValue());
                }
            });

        }

        @Override
        protected Void doInBackground() {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                while (!isCancelled() && builder.hasNext()) {
                    builder.next();
                    setProgress(builder.getPercent());
                }
                Evaluation evaluation = builder.evaluation();
                stopWatch.stop();
                if (evaluation != null) {
                    evaluation.setTotalTimeMillis(stopWatch.getTotalTimeMillis());
                }
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                isSuccess = false;
                errorMessage = e.getMessage();
            }
            setProgress(100);
            if (!isCancelled()) {
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    LoggerUtils.error(log, e);
                }
            }
            return null;
        }

        @Override
        protected void done() {
            setVisible(false);
        }

    } //End of class SwingWorkerConstruction

}