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
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter("Xls data files (*.xls)", DataFileExtension.XLS.getExtension()));
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter("Xls data files (*.xlsx)", DataFileExtension.XLSX.getExtension()));
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter("Csv data files (*.csv)", DataFileExtension.CSV.getExtension()));
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter("Arff data files (*.arff)", DataFileExtension.ARFF.getExtension()));
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter("Json data files (*.json)", DataFileExtension.JSON.getExtension()));
    }

}
