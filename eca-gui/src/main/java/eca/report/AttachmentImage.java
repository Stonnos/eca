package eca.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * Report attachment image model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentImage {

    /**
     * Image title
     */
    private String title;

    /**
     * Image model
     */
    private Image image;
}
