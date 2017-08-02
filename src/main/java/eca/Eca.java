package eca;

import eca.gui.frames.JMainFrame;
import java.awt.EventQueue;

/**
 * Main class.
 * @author Roman Batygin
 */
public class Eca {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JMainFrame().setVisible(true);
            }
        });

    }
}
