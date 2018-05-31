/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.choosers;

import eca.data.DataFileExtension;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Roman Batygin
 */
public class OpenDataFileChooser extends OpenFileChooser {

    public OpenDataFileChooser() {
        for (DataFileExtension extension : DataFileExtension.values()) {
            getChooser().addChoosableFileFilter(
                    new FileNameExtensionFilter(extension.getDescription(), extension.getExtension()));
        }
    }

}
