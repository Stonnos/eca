package eca.buffer;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Implements copying string into system buffer.
 *
 * @author Roman Batygin
 */
public class StringCopier implements Copyable {

    @Getter
    @Setter
    private String copyString;

    @Override
    public void copy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(copyString), null);
    }

}
