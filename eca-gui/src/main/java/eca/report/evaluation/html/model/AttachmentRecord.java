package eca.report.evaluation.html.model;

import lombok.Data;

/**
 * Attachment record model.
 *
 * @author Roman Batygin
 */
@Data
public class AttachmentRecord {

    /**
     * Attachment title
     */
    private String title;

    /**
     * Base64 string value
     */
    private String base64String;
}
