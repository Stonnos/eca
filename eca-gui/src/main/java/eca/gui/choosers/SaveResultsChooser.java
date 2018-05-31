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
public class SaveResultsChooser extends SaveFileChooser {

    public SaveResultsChooser() {
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter(DataFileExtension.XLS.getDescription(),
                        DataFileExtension.XLS.getExtension()));
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter(DataFileExtension.XLSX.getDescription(),
                        DataFileExtension.XLSX.getExtension()));
    }

}
