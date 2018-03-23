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

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JMainFrame().setVisible(true);
                log.info("Eca application was started.");
            }
        });

    }
}
