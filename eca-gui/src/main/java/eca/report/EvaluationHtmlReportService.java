package eca.report;

import eca.config.VelocityConfigService;
import eca.core.evaluation.Evaluation;
import eca.gui.service.ClassifierInputOptionsService;
import eca.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.springframework.util.CollectionUtils;
import weka.core.Attribute;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Implements classification results saving into html file.
 *
 * @author Roman Batygin
 */
public class EvaluationHtmlReportService extends AbstractReportService {

    /**
     * Velocity configuration
     */
    private static final VelocityConfigService VELOCITY_CONFIGURATION =
            VelocityConfigService.getVelocityConfigService();

    private static final String VM_REPORT_TEMPLATE = "vm-templates/evaluationResultsReport.vm";
    private static final String CP1251 = "cp1251";
    private static final String NAN = "NaN";
    private static final String PNG = "PNG";

    private static final String INPUT_OPTIONS_PARAM = "inputOptions";
    private static final String STATISTICS_PARAM = "statistics";
    private static final String CONFUSION_MATRIX_PARAM = "confusionMatrix";
    private static final String CLASSIFICATION_COST_PARAM = "classificationCosts";
    private static final String ATTACHMENTS_PARAM = "attachments";
    private static final String IMAGE_TITLE_FORMAT = "Рис %d. %s";
    private static final String HTML_EXTENSION = ".html";


    @Override
    public void saveReport() throws Exception {
        Template template = VELOCITY_CONFIGURATION.getTemplate(VM_REPORT_TEMPLATE);
        VelocityContext context = new VelocityContext();
        context.put(STATISTICS_PARAM, getEvaluationReport().getStatisticsMap());
        context.put(INPUT_OPTIONS_PARAM, ClassifierInputOptionsService.getClassifierInputOptionsAsHtml
                (getEvaluationReport().getClassifier(), true));
        fillClassificationCosts(context);
        fillConfusionMatrix(context);
        fillAttachments(context);
        String htmlString = mergeContext(template, context);
        FileUtils.write(getFile(), htmlString, Charset.forName(CP1251));
    }

    @Override
    protected void validateFile(File file) {
        if (!file.getName().endsWith(HTML_EXTENSION)) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }

    private void fillClassificationCosts(VelocityContext context) {
        Attribute classAttribute = getEvaluationReport().getData().classAttribute();
        List<ClassificationCostRecord> classificationCostRecordList = new ArrayList<>(classAttribute.numValues());
        Evaluation evaluation = getEvaluationReport().getEvaluation();
        for (int i = 0; i < classAttribute.numValues(); i++) {
            ClassificationCostRecord record = new ClassificationCostRecord();
            record.setClassValue(classAttribute.value(i));
            record.setTpRate(getDecimalFormat().format(evaluation.truePositiveRate(i)));
            record.setFpRate(getDecimalFormat().format(evaluation.falsePositiveRate(i)));
            record.setTnRate(getDecimalFormat().format(evaluation.trueNegativeRate(i)));
            record.setFnRate(getDecimalFormat().format(evaluation.falseNegativeRate(i)));
            record.setRecall(getDecimalFormat().format(evaluation.recall(i)));
            record.setPrecision(getDecimalFormat().format(evaluation.precision(i)));
            record.setFMeasure(getDecimalFormat().format(evaluation.fMeasure(i)));
            double aucValue = evaluation.areaUnderROC(i);
            record.setAucValue(Double.isNaN(aucValue) ? NAN : getDecimalFormat().format(aucValue));
            classificationCostRecordList.add(record);
        }
        context.put(CLASSIFICATION_COST_PARAM, classificationCostRecordList);
    }

    private void fillConfusionMatrix(VelocityContext context) {
        Attribute classAttribute = getEvaluationReport().getData().classAttribute();
        ConfusionMatrixReport confusionMatrixReport = new ConfusionMatrixReport();
        confusionMatrixReport.setClassValues(Utils.getAttributeValues(classAttribute));
        confusionMatrixReport.setConfusionMatrixRecords(new ArrayList<>());
        double[][] confusionMatrix = getEvaluationReport().getEvaluation().confusionMatrix();
        for (int i = 0; i < confusionMatrix.length; i++) {
            ConfusionMatrixRecord record = new ConfusionMatrixRecord();
            record.setClassValue(classAttribute.value(i));
            record.setValues(new ArrayList<>());
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                record.getValues().add(String.valueOf(confusionMatrix[i][j]));
            }
            confusionMatrixReport.getConfusionMatrixRecords().add(record);
        }
        context.put(CONFUSION_MATRIX_PARAM, confusionMatrixReport);
    }

    private void fillAttachments(VelocityContext context) throws IOException {
        List<AttachmentRecord> attachmentRecords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(getEvaluationReport().getAttachmentImages())) {
            for (int i = 0; i < getEvaluationReport().getAttachmentImages().size(); i++) {
                AttachmentImage attachmentImage = getEvaluationReport().getAttachmentImages().get(i);
                try (ByteArrayOutputStream byteArrayImg = new ByteArrayOutputStream()) {
                    ImageIO.write((BufferedImage) attachmentImage.getImage(), PNG, byteArrayImg);
                    String base64Image = Base64.getEncoder().encodeToString( byteArrayImg.toByteArray());
                    AttachmentRecord record = new AttachmentRecord();
                    record.setTitle(String.format(IMAGE_TITLE_FORMAT, i + 1, attachmentImage.getTitle()));
                    record.setBase64String(base64Image);
                    attachmentRecords.add(record);
                }
            }
            context.put(ATTACHMENTS_PARAM, attachmentRecords);
        }
    }

    private String mergeContext(Template template, VelocityContext context) {
        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);
        return stringWriter.toString();
    }
}
