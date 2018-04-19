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
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Xls data files (*.xls)", DataFileExtension.XLS.getExtension()));
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Xls data files (*.xlsx)", DataFileExtension.XLSX.getExtension()));
    }

}