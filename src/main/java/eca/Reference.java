/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca;

import java.awt.*;
import java.io.File;

/**
 * @author Roman93
 */
public class Reference {

    private static File REFERENCE_FILE;

    private static final String FILE_NAME = "ECAManual.pdf";

    static {
        try {
            REFERENCE_FILE = new File("./" + FILE_NAME);
            REFERENCE_FILE.setExecutable(true, false);
        } catch (Exception e) {
        }
    }

    public void openReference() throws Exception {
        if (REFERENCE_FILE == null) {
            throw new Exception("File with name " + FILE_NAME + " doesn't exists!");
        }
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(REFERENCE_FILE);
        } else {
            throw new Exception("Can't open file " + FILE_NAME);
        }
    }
}
