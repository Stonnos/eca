/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.data.db.JdbcQueryExecutor;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.InstancesSetTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Frame for execution database queries.
 *
 * @author Roman Batygin
 */
@Slf4j
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
    private static final int URL_FIELD_LENGTH = 30;
    private static final int USER_FIELD_LENGTH = 10;
    private static final Dimension QUERY_BUTTON_DIM = new Dimension(150, 25);
    private static final Dimension SQL_EDITOR_PREFERRED_SIZE = new Dimension(400, 150);
    private static final String BLUE_STYLE_NAME = "blue";
    private static final String DEFAULT_STYLE_NAME = "default";

    private final JdbcQueryExecutor connection;

    private JTextPane queryArea;
    private JProgressBar progress;
    private JButton executeButton;
    private JButton interruptButton;

    private InstancesSetTable instancesSetTable;

    private QueryWorker worker;

    private JMainFrame parentFrame;

    public QueryFrame(JMainFrame parentFrame, JdbcQueryExecutor connection) {
        this.parentFrame = parentFrame;
        this.connection = connection;
        this.setLayout(new GridBagLayout());
        this.setIconImage(parentFrame.getIconImage());
        this.setResizable(false);
        this.createGUI();
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

        this.pack();
        this.setLocationRelativeTo(parentFrame);
    }

    public List<Instances> getSelectedInstances() {
        int[] selectedRows = instancesSetTable.getSelectedRows();
        ArrayList<Instances> result = new ArrayList<>(selectedRows.length);
        for (int i : selectedRows) {
            result.add(instancesSetTable.getInstancesSetTableModel().getInstances(i));
        }
        return result;
    }

    private void interruptWorker() {
        if (worker != null && !worker.isCancelled()) {
            worker.cancel(true);
        }
    }

    private void closeConnection() {
        SwingUtilities.invokeLater(() -> {
            try {
                connection.close();
            } catch (Exception ex) {
                LoggerUtils.error(log, ex);
            }
        });
    }

    private void createGUI() {
        JPanel paramPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paramPanel.setBorder(PanelBorderUtils.createTitledBorder(DB_TITLE));
        JTextField urlField = new JTextField(URL_FIELD_LENGTH);
        urlField.setText(connection.getConnectionDescriptor().getUrl());
        urlField.setBackground(Color.WHITE);
        urlField.setCaretPosition(0);
        urlField.setEditable(false);
        JTextField userField = new JTextField(USER_FIELD_LENGTH);
        userField.setText(connection.getConnectionDescriptor().getLogin());
        userField.setBackground(Color.WHITE);
        userField.setEditable(false);
        paramPanel.add(new JLabel(URL_TITLE));
        paramPanel.add(urlField);
        paramPanel.add(new JLabel(USER_TITLE));
        paramPanel.add(userField);
        //-----------------------------------------------------
        JPanel queryPanel = new JPanel(new GridBagLayout());
        queryPanel.setBorder(PanelBorderUtils.createTitledBorder(QUERY_TITLE));
        createSqlPaneEditor();
        JScrollPane scrollPanel = new JScrollPane(queryArea);
        executeButton = new JButton(START_BUTTON_TEXT);
        JButton clearButton = new JButton(CLEAR_BUTTON_TEXT);
        interruptButton = new JButton(INTERRUPT_BUTTON_TEXT);
        interruptButton.setEnabled(false);
        //-----------------------------------------
        executeButton.addActionListener(e -> {
            if (queryArea.getText() == null || StringUtils.isEmpty(queryArea.getText().trim())) {
                GuiUtils.showErrorMessageAndRequestFocusOn(QueryFrame.this, queryArea);
                queryArea.setText(StringUtils.EMPTY);
            } else {
                progress.setIndeterminate(true);
                worker = new QueryWorker(queryArea.getText().trim());
                interruptButton.setEnabled(true);
                worker.execute();
            }
        });

        interruptButton.addActionListener(e -> interruptWorker());
        clearButton.addActionListener(e -> queryArea.setText(StringUtils.EMPTY));
        //-----------------------------------------
        executeButton.setPreferredSize(QUERY_BUTTON_DIM);
        clearButton.setPreferredSize(QUERY_BUTTON_DIM);
        interruptButton.setPreferredSize(QUERY_BUTTON_DIM);
        queryPanel.add(scrollPanel, new GridBagConstraints(0, 0, 3, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        queryPanel.add(executeButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 0, 3, 3), 0, 0));
        queryPanel.add(clearButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        queryPanel.add(interruptButton, new GridBagConstraints(2, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 0), 0, 0));

        progress = new JProgressBar();

        instancesSetTable = new InstancesSetTable(this);
        JScrollPane setsPane = new JScrollPane(instancesSetTable);
        setsPane.setBorder(PanelBorderUtils.createTitledBorder(DATA_TITLE));
        setsPane.setPreferredSize(new Dimension(600, 150));

        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(e -> setVisible(false));
        //-----------------------------------------------
        okButton.addActionListener(e -> {
            interruptWorker();
            if (instancesSetTable.getSelectedRows().length != 0) {
                try {
                    for (Instances instances : getSelectedInstances()) {
                        parentFrame.createDataFrame(instances);
                    }
                    dispose();
                } catch (Exception ex) {
                    LoggerUtils.error(log, ex);
                    JOptionPane.showMessageDialog(parentFrame, ex.getMessage(),
                            null, JOptionPane.WARNING_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(QueryFrame.this,
                        CREATE_SAMPLE_ERROR_MESSAGE,
                        null, JOptionPane.WARNING_MESSAGE);
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

    private void createSqlPaneEditor() {
        queryArea = new JTextPane();
        queryArea.setPreferredSize(SQL_EDITOR_PREFERRED_SIZE);
        queryArea.setFont(QUERY_AREA_FONT);
        DefaultStyledDocument styledDocument = (DefaultStyledDocument) queryArea.getStyledDocument();
        //Adding styles
        Style style = styledDocument.addStyle(BLUE_STYLE_NAME, null);
        StyleConstants.setForeground(style, Color.BLUE);
        styledDocument.setDocumentFilter(new SqlHighlightFilter());
    }

    private void highlight() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<String> keywords = Arrays.asList("select", "from");
                StyledDocument document = queryArea.getStyledDocument();
                String content = document.getText(0, document.getLength()).toLowerCase();
                int next = 0;
                for (String word : content.split("\\s")) {
                    next = content.indexOf(word, next);
                    int end = next + word.length();
                    document.setCharacterAttributes(next, end,
                            queryArea.getStyle(keywords.contains(word) ? BLUE_STYLE_NAME : DEFAULT_STYLE_NAME),
                            true);
                    next = end;
                }
            } catch (BadLocationException ex) {
                log.error(ex.getMessage());
            }
        });
    }

    /**
     * Database query worker.
     */
    private class QueryWorker extends SwingWorker<Void, Void> {

        String query;
        Instances data;
        String errorMessage;

        QueryWorker(String query) {
            this.query = query;
        }

        @Override
        protected Void doInBackground() {
            try {
                executeButton.setEnabled(false);
                connection.setSource(query);
                data = connection.loadInstances();
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void done() {
            progress.setIndeterminate(false);
            executeButton.setEnabled(true);
            interruptButton.setEnabled(false);
            if (data != null) {
                instancesSetTable.addInstances(data);
            } else {
                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(QueryFrame.this,
                            errorMessage,
                            null, JOptionPane.WARNING_MESSAGE);
                }
            }
        }

    } //End of class SwingWorkerConstruction

    /**
     * Implements SQL keywords highlight filter.
     */
    private class SqlHighlightFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (StringUtils.isEmpty(string)) {
                fb.insertString(offset, string, attr);
            } else {
                super.insertString(fb, offset, string, attr);
                highlight();
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (length == 0) {
                fb.remove(offset, length);
            } else {
                super.remove(fb, offset, length);
                highlight();
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (length == 0 && StringUtils.isEmpty(text)) {
                fb.replace(offset, length, text, attrs);
            } else {
                super.replace(fb, offset, length, text, attrs);
                highlight();
            }
        }
    }
}
