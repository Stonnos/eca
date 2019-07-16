/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca;

import eca.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;

/**
 * Implements working with ECA user manual.
 *
 * @author Roman Batygin
 */
@Slf4j
public class Reference {

    private static File REFERENCE_FILE;
    private static final String FILE_NAME = "ECAManual.pdf";
    private static Reference reference;

    static {
        try {
            REFERENCE_FILE = new File(FileUtils.getCurrentDir(), FILE_NAME);
            REFERENCE_FILE.setExecutable(true, false);
        } catch (Exception ex) {
            log.error("Can't load manual", ex);
        }
    }

    private Reference() {
    }

    /**
     * Gets reference object.
     *
     * @return reference object
     */
    public static Reference getReference() {
        if (reference == null) {
            reference = new Reference();
        }
        return reference;
    }

    /**
     * Opens {@value FILE_NAME} file.
     *
     * @throws Exception if user manual file does not exists
     */
    public void openReference() throws Exception {
        if (REFERENCE_FILE == null) {
            throw new Exception(String.format("Reference file with name %s does not exists!", FILE_NAME));
        }
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(REFERENCE_FILE);
        } else {
            throw new Exception(String.format("Can not open reference file %s", FILE_NAME));
        }
    }
}
