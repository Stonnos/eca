package eca.logging;

import ch.qos.logback.core.OutputStreamAppender;
import eca.gui.ConsoleTextArea;

import java.io.OutputStream;

/**
 * @author Roman Batygin
 */

public class JTextAreaAppender<E> extends OutputStreamAppender<E> {

    public void start() {
        OutputStream targetStream = new JTextAreaOutputStream(ConsoleTextArea.getTextArea());
        this.setOutputStream(targetStream);
        super.start();
    }
}
