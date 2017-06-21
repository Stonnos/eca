/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.choosers;

import eca.io.SerializedObject;

import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Рома
 */
public class OpenModelChooser extends OpenFileChooser {
    
    public OpenModelChooser() {
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Txt data files (*.txt)","txt"));
    }

}
