package eca.report;

import eca.report.evaluation.html.model.AttachmentRecord;
import eca.report.model.AttachmentImage;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Report helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ReportHelper {

    private static final String PNG = "PNG";

    /**
     * Converts to attachment record.
     *
     * @param attachmentImage - attachment image
     * @return attachment record
     * @throws IOException in case of I/O error
     */
    public static AttachmentRecord toAttachmentRecord(AttachmentImage attachmentImage) throws IOException {
        @Cleanup ByteArrayOutputStream byteArrayImg = new ByteArrayOutputStream();
        ImageIO.write((BufferedImage) attachmentImage.getImage(), PNG, byteArrayImg);
        String base64Image = Base64.getEncoder().encodeToString(byteArrayImg.toByteArray());
        AttachmentRecord record = new AttachmentRecord();
        record.setTitle(attachmentImage.getTitle());
        record.setBase64String(base64Image);
        return record;
    }
}