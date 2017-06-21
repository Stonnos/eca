/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import eca.gui.actions.Actionable;
/**
 *
 * @author Рома
 */
public class LoadDialog extends JDialog implements ExecutorDialog {
    
    private final Actionable action;
    private final JProgressBar progress;
    private SwingWorkerConstruction worker;
    
    private boolean isSuccess = true;
    private String errorMessage;
   
    public LoadDialog(Window parent, Actionable action, String msg) {
        super(parent,"");
        this.setModal(true);
        this.action = action;
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
        progress.setIndeterminate(true);
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
    
    /**
     * 
     */
    private class SwingWorkerConstruction extends SwingWorker<Void, Void> {
       
        @Override
        protected Void doInBackground() {
            try {
                action.action();
            }
            catch (Throwable e) {
                e.printStackTrace();
                isSuccess = false;
                errorMessage = e.getMessage();
            }
            setProgress(100);
            return null;
        }
        
        @Override
        protected void done() {
            setVisible(false);
        }
        
    } //End of class SwingWorkerConstruction
}
