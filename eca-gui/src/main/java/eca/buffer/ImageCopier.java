package eca.buffer;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * Implements copying image into system buffer.
 *
 * @author Roman Batygin
 */
public class ImageCopier implements Transferable, Copyable {

    @Getter
    @Setter
    private Image image;

    private static final DataFlavor[] FLAVORS = {
            DataFlavor.imageFlavor,
    };

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return FLAVORS.clone();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(FLAVORS[0]);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (isDataFlavorSupported(flavor)) {
            return image;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public void copy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(this, null);
    }

}
