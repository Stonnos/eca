package eca.gui.logging;

import ch.qos.logback.core.OutputStreamAppender;
import eca.gui.ConsoleTextArea;

import java.io.OutputStream;

/**
 * Swing text area appender implementation.
 *
 * @author Roman Batygin
 */
public class JTextAreaAppender<E> extends OutputStreamAppender<E> {

    @Override
    public void start() {
        OutputStream targetStream = new JTextAreaOutputStream(ConsoleTextArea.getTextArea());
        this.setOutputStream(targetStream);
        super.start();
    }
}
