package eca.gui.listeners;

import eca.Reference;
import eca.gui.logging.LoggerUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implements action listener to open ECA manual.
 *
 * @author Roman Batygin
 */
@Slf4j
@AllArgsConstructor
public class ReferenceListener implements ActionListener {

    private Component parent;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            Reference reference = Reference.getReference();
            reference.openReference();
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            JOptionPane.showMessageDialog(parent, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
        }
    }
}
