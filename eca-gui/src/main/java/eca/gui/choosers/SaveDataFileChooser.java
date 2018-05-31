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
public class SaveDataFileChooser extends SaveFileChooser {

    public SaveDataFileChooser() {
        for (DataFileExtension dataFileExtension : DataFileExtension.values()) {
            getChooser().addChoosableFileFilter(
                    new FileNameExtensionFilter(dataFileExtension.getDescription(), dataFileExtension.getExtension()));
        }
    }

}
