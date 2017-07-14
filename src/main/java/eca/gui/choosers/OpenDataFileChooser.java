/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.choosers;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import eca.core.converters.XLSLoader;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author Рома
 */
public class OpenDataFileChooser extends OpenFileChooser {

    public OpenDataFileChooser() {
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Xls filteredData files (*.xls)", "xls"));
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Xls filteredData files (*.xlsx)", "xlsx"));
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Csv filteredData files (*.csv)", "csv"));
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Arff filteredData files (*.arff)", "arff"));
    }

}
