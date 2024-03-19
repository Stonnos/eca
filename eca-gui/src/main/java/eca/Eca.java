package eca;

import eca.gui.frames.JMainFrame;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * Main class.
 *
 * @author Roman Batygin
 */
@Slf4j
public class Eca {

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JMainFrame mainFrame = new JMainFrame();
            mainFrame.setVisible(true);
            mainFrame.initializeMessageListenerContainer();
            mainFrame.initializeUploadInstancesClient();
            log.info("Eca application was started.");
        });

    }
}
