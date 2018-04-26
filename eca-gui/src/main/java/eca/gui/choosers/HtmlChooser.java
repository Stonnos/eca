package eca.gui.choosers;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Roman Batygin
 */
public class HtmlChooser extends SaveFileChooser {

    public HtmlChooser() {
        getChooser().addChoosableFileFilter(
                new FileNameExtensionFilter("Html files (*.html)", "html"));
    }

}
