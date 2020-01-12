package eca.gui.listeners;

import eca.gui.ResizeableImage;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Resizable image listener.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class ResizableImageListener<E extends JPanel & ResizeableImage> implements MouseWheelListener {

    private final E component;

    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        if (event.isControlDown()) {
            if (event.getWheelRotation() < 0) {
                component.increaseImage();
            } else {
                component.decreaseImage();
            }
        } else {
            //Scroll panel otherwise
            component.getParent().dispatchEvent(event);
        }
    }
}
