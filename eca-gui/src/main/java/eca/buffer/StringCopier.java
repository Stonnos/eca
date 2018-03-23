package eca.buffer;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Implements copying string into system buffer.
 *
 * @author Roman Batygin
 */
public class StringCopier implements Copyable {

    private String copyString;

    /**
     * Returns string for copying.
     *
     * @return string object.
     */
    public String getCopyString() {
        return copyString;
    }

    /**
     * Sets the string for copying.
     *
     * @param copyString string object
     */
    public void setCopyString(String copyString) {
        this.copyString = copyString;
    }

    @Override
    public void copy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(copyString), null);
    }

}
