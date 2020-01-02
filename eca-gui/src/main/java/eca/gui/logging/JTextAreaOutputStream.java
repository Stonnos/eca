package eca.gui.logging;

import javax.swing.JTextArea;
import javax.swing.text.Document;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author Roman Batygin
 */
public class JTextAreaOutputStream extends OutputStream {

    private JTextArea textArea;

    public JTextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
    }

    @Override
    public void write(byte b[], int off, int len) {
        Objects.requireNonNull(b, "Buffer is not Specified!");
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        String text = new String(b, off, len);
        textArea.append(text);
        Document document = textArea.getDocument();
        int caretPosition = document.getLength() > 0 ? textArea.getDocument().getLength() - 1 : 0;
        textArea.setCaretPosition(caretPosition);
    }
}
