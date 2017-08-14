package eca.io.buffer;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author Roman Batygin
 */
public class StringCopier implements Copyable {

    private String copyString;

    public StringCopier() {
    }

    public StringCopier(String copyString) {
        this.copyString = copyString;
    }

    public String getCopyString() {
        return copyString;
    }

    public void setCopyString(String copyString) {
        this.copyString = copyString;
    }

    @Override
    public void copy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(copyString), null);
    }

}
