/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.db.DataBaseConnection;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */
public class QueryFrame extends JFrame {

    private static final String DB_TITLE = "База данных";
    private static final String URL_TITLE = "URL:";
    private static final String USER_TITLE = "User:";
    private static final String QUERY_TITLE = "SELECT запрос";
    private static final String DATA_TITLE = "Данные";
    private static final String INTERRUPT_BUTTON_TEXT = "Прервать";
    private static final String CLEAR_BUTTON_TEXT = "Очистить";
    private static final String START_BUTTON_TEXT = "Выполнить";

    private static final Font QUERY_AREA_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final String CREATE_SAMPLE_ERROR_MESSAGE = "Необходимо сформировать выборку и выбрать ее в списке!";

    private final DataBaseConnection connection;

    private JTextArea queryArea;
    private JProgressBar progress;
    private JList<String> sets;
    private ListModel model;
    private JButton execute;
    private JButton interrupt;

    private SwingWorkerConstruction worker;

    private JMainFrame parent;

    public QueryFrame(JMainFrame parent, DataBaseConnection connection) {
        this.parent = parent;
        this.connection = connection;
        this.setLayout(new GridBagLayout());
        this.setIconImage(parent.getIconImage());
        this.setResizable(false);
        this.makeGUI();
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                interruptWorker();
                closeConnection();
            }

            @Override
            public void windowClosed(WindowEvent evt) {
                closeConnection();
            }

        });
        //-----------------------------------
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public java.util.ArrayList<Instances> instances() {
        int[] indices = sets.getSelectedIndices();
        ArrayList<Instances> result = new ArrayList<>(indices.length);
        for (int i : indices) {
            result.add(model.instance(i));
        }
        return result;
    }

    private void interruptWorker() {
        if (worker != null && !worker.isCancelled()) {
            worker.cancel(true);
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void makeGUI() {
        JPanel paramPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paramPanel.setBorder(PanelBorderUtils.createTitledBorder(DB_TITLE));
        JTextField url = new JTextField(30);
        url.setText(connection.getConnectionDescriptor().getUrl());
        url.setBackground(Color.WHITE);
        url.setCaretPosition(0);
        url.setEditable(false);
        JTextField user = new JTextField(10);
        user.setText(connection.getConnectionDescriptor().getLogin());
        user.setBackground(Color.WHITE);
        user.setEditable(false);
        paramPanel.add(new JLabel(URL_TITLE));
        paramPanel.add(url);
        paramPanel.add(new JLabel(USER_TITLE));
        paramPanel.add(user);
        //-----------------------------------------------------
        JPanel queryPanel = new JPanel(new GridBagLayout());
        queryPanel.setBorder(PanelBorderUtils.createTitledBorder(QUERY_TITLE));
        queryArea = new JTextArea(10, 20);
        queryArea.setWrapStyleWord(true);
        queryArea.setLineWrap(true);
        queryArea.setFont(QUERY_AREA_FONT);
        JScrollPane scrollPanel = new JScrollPane(queryArea);
        execute = new JButton(START_BUTTON_TEXT);
        JButton clear = new JButton(CLEAR_BUTTON_TEXT);
        interrupt = new JButton(INTERRUPT_BUTTON_TEXT);
        interrupt.setEnabled(false);
        //-----------------------------------------
        execute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                progress.setIndeterminate(true);
                worker = new SwingWorkerConstruction(queryArea.getText());
                interrupt.setEnabled(true);
                worker.execute();
            }
        });

        interrupt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                interruptWorker();
            }
        });

        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                queryArea.setText(StringUtils.EMPTY);
            }
        });
        //-----------------------------------------
        Dimension dim = new Dimension(150, 25);
        execute.setPreferredSize(dim);
        clear.setPreferredSize(dim);
        interrupt.setPreferredSize(dim);
        queryPanel.add(scrollPanel, new GridBagConstraints(0, 0, 3, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        queryPanel.add(execute, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 0, 3, 3), 0, 0));
        queryPanel.add(clear, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        queryPanel.add(interrupt, new GridBagConstraints(2, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 0), 0, 0));
        //-----------------------------------------------------
        progress = new JProgressBar();
        //-----------------------------------------------------
        model = new ListModel();
        sets = new JList<>(model);
        JScrollPane setsPane = new JScrollPane(sets);
        setsPane.setBorder(PanelBorderUtils.createTitledBorder(DATA_TITLE));
        setsPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sets.addMouseListener(new MouseAdapter() {

            InstancesFrame instancesFrame;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int i = sets.locationToIndex(e.getPoint());
                    instancesFrame = new InstancesFrame(model.instance(i), QueryFrame.this);
                    instancesFrame.setVisible(true);
                }
            }

        });
        //-----------------------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        //-----------------------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                interruptWorker();
                if (sets.getSelectedIndices().length != 0) {
                    try {
                        for (Instances ins : instances()) {
                            parent.createDataFrame(ins);
                        }
                        dispose();
                    } catch (Throwable ex) {
                        JOptionPane.showMessageDialog(parent,
                                ex.getMessage(),
                                null, JOptionPane.WARNING_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(QueryFrame.this,
                            CREATE_SAMPLE_ERROR_MESSAGE,
                            null, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //-----------------------------------------------------
        this.add(paramPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(queryPanel, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(progress, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(setsPane, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

    /**
     *
     */
    private static class ListModel extends DefaultListModel<String> {

        ArrayList<Instances> instances = new ArrayList<>();

        public Instances instance(int i) {
            return instances.get(i);
        }

        public void addInstances(Instances data) {
            instances.add(data);
            this.addElement(data.relationName());
        }

    } //End of class ListModel

    /**
     *
     */
    private class SwingWorkerConstruction extends SwingWorker<Void, Void> {

        String query;
        Instances data;
        String errorMessage;

        SwingWorkerConstruction(String query) {
            this.query = query;
        }

        @Override
        protected Void doInBackground() {
            try {
                execute.setEnabled(false);
                data = connection.executeQuery(query);
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void done() {
            progress.setIndeterminate(false);
            execute.setEnabled(true);
            interrupt.setEnabled(false);
            if (data != null) {
                model.addInstances(data);
                sets.setSelectedIndex(0);
            } else {
                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(QueryFrame.this,
                            errorMessage,
                            null, JOptionPane.WARNING_MESSAGE);
                }
            }
        }

    } //End of class SwingWorkerConstruction

}
