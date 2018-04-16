package eca.gui.dialogs;

import eca.config.ApplicationConfigService;
import eca.gui.ButtonUtils;
import eca.gui.dictionary.CommonDictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implements dialog for setting options for automated random forests algorithm.
 *
 * @author Roman Batygin
 */
public class AutomatedRandomForestsOptionsDialog extends JDialog {

    private static final ApplicationConfigService CONFIG_SERVICE =
            ApplicationConfigService.getApplicationConfigService();

    private static final String OPTIONS_TITLE = "Настройки параметров";
    private static final String NUM_ITERATIONS_TITLE = "Число итераций:";
    private static final String NUM_THREADS_TITLE = "Число потоков:";

    private static final int MIN_ITERATIONS = 10;
    private static final int MAX_ITERATIONS = 1000;

    private final JSpinner iterationsSpinner = new JSpinner();
    private final JSpinner threadsSpinner = new JSpinner();

    private boolean dialogResult;

    public AutomatedRandomForestsOptionsDialog(Window window, int numIterations, int numThreads) {
        super(window, OPTIONS_TITLE);
        this.setModal(true);
        this.setResizable(false);
        this.setLayout(new GridBagLayout());
        this.createGUI(numIterations, numThreads);
        this.pack();
        this.setLocationRelativeTo(window);
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public int getNumIterations() {
        return Integer.valueOf(iterationsSpinner.getValue().toString());
    }

    public int getNumThreads() {
        return Integer.valueOf(threadsSpinner.getValue().toString());
    }

    private void createGUI(int numIterations, int numThreads) {
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.add(new JLabel(NUM_ITERATIONS_TITLE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(iterationsSpinner, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(NUM_THREADS_TITLE),
                new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(threadsSpinner, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));
        //-------------------------------------------------------
        iterationsSpinner.setModel(new SpinnerNumberModel(numIterations, MIN_ITERATIONS, MAX_ITERATIONS, 1));
        threadsSpinner.setModel(
                new SpinnerNumberModel(numThreads, CommonDictionary.MIN_THREADS_NUM, CONFIG_SERVICE
                        .getApplicationConfig().getMaxThreads().intValue(), 1));
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = false;
                setVisible(false);
            }
        });
        //-----------------------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = true;
                setVisible(false);
            }
        });
        //------------------------------------
        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

}
