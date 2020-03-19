package eca.report.evaluation.html;

import com.google.common.collect.ImmutableList;
import eca.config.VelocityConfigService;
import eca.core.evaluation.Evaluation;
import eca.report.ReportGenerator;
import eca.report.evaluation.AbstractEvaluationReportService;
import eca.report.evaluation.html.model.ClassificationCostRecord;
import eca.report.evaluation.html.model.ConfusionMatrixRecord;
import eca.report.evaluation.html.model.ConfusionMatrixReport;
import eca.report.evaluation.html.model.LogisticCoefficientsData;
import eca.report.evaluation.html.model.LogisticCoefficientsRecord;
import eca.report.model.DecisionTreeReport;
import eca.report.model.EvaluationReport;
import eca.report.model.LogisticReport;
import eca.report.model.NeuralNetworkReport;
import eca.util.Utils;
import eca.util.VelocityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static eca.report.ReportHelper.toAttachmentRecord;

/**
 * Implements classification results saving into html file.
 *
 * @author Roman Batygin
 */
public class EvaluationHtmlReportService extends AbstractEvaluationReportService {

    private static final String VM_REPORT_TEMPLATE = "vm-templates/evaluationResultsReport.vm";
    private static final String VM_TEMPLATE_DECISION_TREE_REPORT = "vm-templates/decisionTreeResultsReport.vm";
    private static final String VM_TEMPLATE_NEURAL_NETWORK_REPORT = "vm-templates/neuralNetworkResultsReport.vm";
    private static final String VM_TEMPLATE_LOGISTIC_REPORT = "vm-templates/logisticResultsReport.vm";
    private static final String NAN = "NaN";

    private static final String INPUT_OPTIONS_PARAM = "inputOptions";
    private static final String STATISTICS_PARAM = "statistics";
    private static final String CONFUSION_MATRIX_PARAM = "confusionMatrix";
    private static final String CLASSIFICATION_COST_PARAM = "classificationCosts";
    private static final String ROC_CURVE_IMAGE_PARAM = "rocCurveImage";
    private static final String NETWORK_IMAGE_PARAM = "networkImage";
    private static final String TREE_IMAGE_PARAM = "treeImage";
    private static final String LOGISTIC_COEFFICIENTS_PARAM = "logisticCoefficients";
    private static final String HTML_EXTENSION = ".html";

    private final List<EvaluationReportHandler> evaluationReportHandlers =
            ImmutableList.of(new DecisionTreeReportHandler(), new NeuralNetworkReportHandler(),
                    new LogisticReportHandler());
    private final CommonReportHandler commonReportHandler = new CommonReportHandler();

    @Override
    public void saveReport() throws Exception {
        EvaluationReportHandler evaluationReportHandler = evaluationReportHandlers.stream()
                .filter(handler -> handler.canHandle(getEvaluationReport()))
                .findFirst()
                .orElse(null);
        if (evaluationReportHandler != null) {
            evaluationReportHandler.saveReport(getEvaluationReport());
        } else {
            commonReportHandler.saveReport(getEvaluationReport());
        }
    }

    @Override
    protected void validateFile(File file) {
        if (!file.getName().endsWith(HTML_EXTENSION)) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }

    /**
     * Evaluation report handler.
     *
     * @param <T> - evaluation report generic type
     */
    @RequiredArgsConstructor
    private abstract class EvaluationReportHandler<T extends EvaluationReport> {

        private final Class<T> reportClazz;
        private final String templateName;

        boolean canHandle(T report) {
            return reportClazz.isAssignableFrom(report.getClass());
        }

        void saveReport(T report) throws Exception {
            Template template = VelocityConfigService.getTemplate(templateName);
            VelocityContext context = buildContext(report);
            VelocityUtils.mergeAndWrite(getFile(), template, context);
        }

        VelocityContext buildContext(T report) throws Exception {
            VelocityContext context = new VelocityContext();
            context.put(STATISTICS_PARAM, getEvaluationReport().getStatisticsMap());
            context.put(INPUT_OPTIONS_PARAM, ReportGenerator.getClassifierInputOptionsAsHtml
                    (getEvaluationReport().getClassifier(), true));
            fillClassificationCosts(context);
            fillConfusionMatrix(context);
            context.put(ROC_CURVE_IMAGE_PARAM, toAttachmentRecord(report.getRocCurveImage()));
            internalFillContext(context, report);
            return context;
        }

        abstract void internalFillContext(VelocityContext context, T report) throws Exception;

        void fillClassificationCosts(VelocityContext context) {
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

        void fillConfusionMatrix(VelocityContext context) {
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
                    record.getValues().add((int) confusionMatrix[i][j]);
                }
                confusionMatrixReport.getConfusionMatrixRecords().add(record);
            }
            context.put(CONFUSION_MATRIX_PARAM, confusionMatrixReport);
        }
    }

    /**
     * Decision tree report handler.
     */
    private class DecisionTreeReportHandler extends EvaluationReportHandler<DecisionTreeReport> {

        DecisionTreeReportHandler() {
            super(DecisionTreeReport.class, VM_TEMPLATE_DECISION_TREE_REPORT);
        }

        @Override
        void internalFillContext(VelocityContext context, DecisionTreeReport report) throws Exception {
            context.put(TREE_IMAGE_PARAM, toAttachmentRecord(report.getTreeImage()));
        }
    }

    /**
     * Neural network report handler.
     */
    private class NeuralNetworkReportHandler extends EvaluationReportHandler<NeuralNetworkReport> {

        NeuralNetworkReportHandler() {
            super(NeuralNetworkReport.class, VM_TEMPLATE_NEURAL_NETWORK_REPORT);
        }

        @Override
        void internalFillContext(VelocityContext context, NeuralNetworkReport report) throws Exception {
            context.put(NETWORK_IMAGE_PARAM, toAttachmentRecord(report.getNetworkImage()));
        }
    }

    /**
     * Logistic report handler.
     */
    private class LogisticReportHandler extends EvaluationReportHandler<LogisticReport> {

        static final String INTERCEPT = "Intercept";
        static final String ATTR_TEXT = "Атрибут";
        static final String CLASS_FORMAT = "Класс %d";

        LogisticReportHandler() {
            super(LogisticReport.class, VM_TEMPLATE_LOGISTIC_REPORT);
        }

        @Override
        void internalFillContext(VelocityContext context, LogisticReport report) throws Exception {
            context.put(LOGISTIC_COEFFICIENTS_PARAM, fillLogisticCoefficientsData(report));
        }

        LogisticCoefficientsData fillLogisticCoefficientsData(LogisticReport report) {
            LogisticCoefficientsData logisticCoefficientsData = new LogisticCoefficientsData();
            logisticCoefficientsData.setHeaders(getCoefficientsHeaders(report));
            logisticCoefficientsData.setCoefficientsRecords(fillLogisticCoefficientsRecords(report));
            return logisticCoefficientsData;
        }

        List<String> getCoefficientsHeaders(LogisticReport report) {
            Attribute classAttribute = report.getData().classAttribute();
            List<String> headers = newArrayList();
            headers.add(ATTR_TEXT);
            List<String> values = IntStream.range(0, classAttribute.numValues()).mapToObj(
                    i -> String.format(CLASS_FORMAT, i - 1)).collect(Collectors.toList());
            headers.addAll(values);
            return headers;
        }

        List<LogisticCoefficientsRecord> fillLogisticCoefficientsRecords(LogisticReport report) {
            Attribute classAttribute = report.getData().classAttribute();
            double[][] coefficients = report.getLogisticCoefficientsModel().getLogistic().coefficients();
            return IntStream.range(0, coefficients.length)
                    .mapToObj(i -> {
                        LogisticCoefficientsRecord record = new LogisticCoefficientsRecord();
                        record.setAttrValue(getAttributeName(i, report.getLogisticCoefficientsModel().getMeta()));
                        final int row = i;
                        List<String> coefficientsList = IntStream.range(0, classAttribute.numValues() - 1).mapToObj(
                                j -> getDecimalFormat().format(coefficients[row][j])).collect(Collectors.toList());
                        record.setCoefficients(coefficientsList);
                        return record;
                    }).collect(Collectors.toList());
        }

        String getAttributeName(int attrIndex, Instances data) {
            return attrIndex == 0 ? INTERCEPT : data.attribute(attrIndex - 1).name();
        }
    }

    /**
     * Common report handler.
     */
    private class CommonReportHandler extends EvaluationReportHandler<EvaluationReport> {

        CommonReportHandler() {
            super(EvaluationReport.class, VM_REPORT_TEMPLATE);
        }

        @Override
        void internalFillContext(VelocityContext context, EvaluationReport report) {
            // Not implemented
        }
    }
}
