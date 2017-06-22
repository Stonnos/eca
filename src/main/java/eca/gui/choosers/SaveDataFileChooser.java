/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.choosers;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

import eca.core.converters.XLSSaver;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

/**
 *
 * @author Рома
 */
public class SaveDataFileChooser extends SaveFileChooser {

    public SaveDataFileChooser() {
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Xls getData files (*.xls)", "xls"));
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Xls getData files (*.xlsx)", "xlsx"));
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Csv getData files (*.csv)", "csv"));
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Arff getData files (*.arff)", "arff"));
    }

}
