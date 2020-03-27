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

    private static final String FILE_NAME = "ECAManual.pdf";

    private static File referenceFile;
    private static Reference reference;

    static {
        try {
            referenceFile = new File(FileUtils.getCurrentDir(), FILE_NAME);
            if (!referenceFile.setExecutable(true, false)) {
                log.error("Can't load manual");
            }
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
        if (referenceFile == null) {
            throw new Exception(String.format("Reference file with name %s does not exists!", FILE_NAME));
        }
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(referenceFile);
        } else {
            throw new Exception(String.format("Can not open reference file %s", FILE_NAME));
        }
    }
}
