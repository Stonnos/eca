/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.ensemble.IterativeBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 *
 * @author Рома
 */
public class ClassifierBuilderDialog extends JDialog implements ExecutorDialog {

    private static final long DELAY = 300L;

    private final IterativeBuilder builder;
    private final JProgressBar progress;
    private SwingWorkerConstruction worker;
    
    private boolean isSuccess = true;
    private String errorMessage;
    
    public ClassifierBuilderDialog(Window parent, IterativeBuilder builder, String msg) {
        super(parent,"");
        this.setModal(true);
        this.builder = builder;
        this.setResizable(false);
        this.setLayout(new GridBagLayout());
        //---------------------------------------------------
        this.addWindowListener(new WindowAdapter() {
             @Override
             public void windowClosing(WindowEvent evt) {
                 worker.cancel(true);
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
        
        public SwingWorkerConstruction() {
            this.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equals(evt.getPropertyName())) {
                        progress.setValue((Integer)evt.getNewValue());
                    }
                }
            });

        }
        
        @Override
        protected Void doInBackground() {

            try {
                while (!isCancelled() && builder.hasNext()) {
                    builder.next();
                    setProgress(builder.getPercent());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                isSuccess = false;
                errorMessage = e.getMessage();
            }

            setProgress(100);

            if (!isCancelled()) {
                try {
                    Thread.sleep(DELAY);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();    
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
