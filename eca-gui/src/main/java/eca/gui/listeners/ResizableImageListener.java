package eca.gui.listeners;

import eca.gui.ResizeableImage;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Resizable image listener.
 *
 * @author Roman Batygin
 */
public class ResizableImageListener implements MouseWheelListener {

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof ResizeableImage)) {
            throw new IllegalStateException(
                    String.format("Component must implements %s interface!", ResizeableImage.class.getName()));
        }
        if (event.isControlDown()) {
            ResizeableImage resizeableImage = (ResizeableImage) component;
            if (event.getWheelRotation() < 0) {
                resizeableImage.increaseImage();
            } else {
                resizeableImage.decreaseImage();
            }
        } else {
            //Scroll panel otherwise
            component.getParent().dispatchEvent(event);
        }
    }
}
