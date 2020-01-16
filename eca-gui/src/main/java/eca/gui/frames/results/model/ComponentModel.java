package eca.gui.frames.results.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

/**
 * Component model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class ComponentModel {

    /**
     * Component title
     */
    private String title;

    /**
     * Component object
     */
    private Component component;
}
