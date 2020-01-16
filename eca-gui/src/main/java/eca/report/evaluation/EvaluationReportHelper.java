package eca.report.evaluation;

import eca.data.FileUtils;
import eca.report.evaluation.html.EvaluationHtmlReportService;
import eca.report.evaluation.xls.EvaluationXlsReportService;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Evaluation report helper.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EvaluationReportHelper {

    private static final String HTML_EXTENSION = ".html";
    private static final String INVALID_REPORT_EXTENSION = "Система не поддерживает отчеты с расширением %s";

    /**
     * Save evaluation results report.
     *
     * @param evaluationReport - evaluation report
     * @param file             - target file
     * @param decimalFormat    - decimal format
     * @throws Exception in case or error
     */
    public static void saveReport(EvaluationReport evaluationReport, File file, DecimalFormat decimalFormat)
            throws Exception {
        AbstractEvaluationReportService reportService = createEvaluationReportService(file);
        reportService.setFile(file);
        reportService.setDecimalFormat(decimalFormat);
        reportService.setEvaluationReport(evaluationReport);
        reportService.saveReport();
    }

    private static AbstractEvaluationReportService createEvaluationReportService(File file) {
        if (FileUtils.isXlsExtension(file.getName())) {
            return new EvaluationXlsReportService();
        } else if (file.getName().endsWith(HTML_EXTENSION)) {
            return new EvaluationHtmlReportService();
        } else {
            throw new IllegalArgumentException(String.format(INVALID_REPORT_EXTENSION, file.getName()));
        }
    }
}
