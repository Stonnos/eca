/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.text.LengthDocument;

import javax.swing.*;
import java.awt.*;

/**
 * Implements dialog with text fields matrix.
 *
 * @author Roman Batygin
 */
public class JTextFieldMatrixDialog extends JDialog {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final int MAX_FIELD_LENGTH = 255;

    private final JTextField[] texts;

    private boolean dialogResult;

    public JTextFieldMatrixDialog(Window parent, String title, String[] names, String[] values, int rows,
                                  int length) {
        super(parent, title);
        this.setLayout(new GridBagLayout());
        this.setModal(true);
        this.setResizable(false);
        GuiUtils.setIcon(this, CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON));
        JLabel[] labels = new JLabel[rows];
        texts = new JTextField[rows];
        JPanel panel = new JPanel(new GridLayout(rows, 2, 0, 10));
        //-------------------------------
        for (int i = 0; i < rows; i++) {
            labels[i] = new JLabel(names[i]);
            texts[i] = new JTextField(length);
            texts[i].setDocument(new LengthDocument(MAX_FIELD_LENGTH));
            texts[i].setText(values[i]);
            panel.add(labels[i]);
            panel.add(texts[i]);
        }
        //---------------------------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        okButton.addActionListener(e -> {
            dialogResult = true;
            setVisible(false);
        });
        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });
        //---------------------------------------------------------
        this.add(panel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 7, 15, 7), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.EAST, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.WEST, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
        texts[0].requestFocusInWindow();
    }

    public String valueAt(int row) {
        return texts[row].getText().trim();
    }

    public boolean dialogResult() {
        return dialogResult;
    }
}
