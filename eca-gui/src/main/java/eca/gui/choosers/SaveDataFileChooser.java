package eca.gui.choosers;

import eca.data.DataFileExtension;

import javax.swing.filechooser.FileNameExtensionFilter;

import static eca.data.FileUtils.SAVE_DATA_EXTENSIONS;

/**
 * @author Roman Batygin
 */
public class SaveDataFileChooser extends SaveFileChooser {

    public SaveDataFileChooser() {
        for (DataFileExtension dataFileExtension : SAVE_DATA_EXTENSIONS) {
            getChooser().addChoosableFileFilter(
                    new FileNameExtensionFilter(dataFileExtension.getDescription(), dataFileExtension.getExtension()));
        }
    }

}
