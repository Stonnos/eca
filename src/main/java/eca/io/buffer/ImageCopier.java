/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.io.buffer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
/**
 *
 * @author Рома
 */
public class ImageCopier implements Transferable, Copyable {

    private Image image;

    private static final DataFlavor[] FLAVORS = {
        DataFlavor.imageFlavor,
    };
    
    public ImageCopier(Image image) {
        this.setImage(image);
    }
    
    public ImageCopier() {
    }
    
    public final void setImage(Image image) {
        this.image = image;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return (DataFlavor[])FLAVORS.clone();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
         return flavor.equals(FLAVORS[0]);
    }

    @Override
    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException
    {
        if (isDataFlavorSupported(flavor)) {
            return (Object)image;
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
