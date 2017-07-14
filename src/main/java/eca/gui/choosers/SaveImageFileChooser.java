/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.choosers;

import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Рома
 */
public class SaveImageFileChooser extends SaveFileChooser {

    public SaveImageFileChooser() {
        chooser.addChoosableFileFilter(
                new FileNameExtensionFilter("PNG data files (*.png)", "png"));
    }

}
