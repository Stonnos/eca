/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.choosers;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * @author Roman Batygin
 */
public abstract class SaveFileChooser {

    private JFileChooser chooser = new JFileChooser();

    protected SaveFileChooser() {
        chooser.setCurrentDirectory(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
    }

    public void setSelectedFile(File file) {
        chooser.setSelectedFile(file);
    }

    public File getSelectedFile(Component parent) {
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            for (FileFilter filter : chooser.getChoosableFileFilters()) {
                if (filter.accept(file)) {
                    return file;
                }
            }
            FileNameExtensionFilter ext = (FileNameExtensionFilter) chooser.getFileFilter();
            file = new File(String.format("%s.%s", file.getAbsolutePath(), ext.getExtensions()[0]));
            return file;
        } else {
            return null;
        }
    }

    public JFileChooser getChooser() {
        return chooser;
    }
}
