package eca.report;

import lombok.Getter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Report service abstract class.
 *
 * @author Roman Batygin
 */
public abstract class AbstractReportService {

    @Getter
    private EvaluationReport evaluationReport;

    @Getter
    private File file;

    @Getter
    private DecimalFormat decimalFormat;

    /**
     * Sets file for saving report.
     *
     * @param file - file
     */
    public void setFile(File file) {
        Objects.requireNonNull(file, "file isn't specified!");
        validateFile(file);
        this.file = file;
    }

    /**
     * Sets evaluation report.
     *
     * @param evaluationReport - evaluation report
     */
    public void setEvaluationReport(EvaluationReport evaluationReport) {
        Objects.requireNonNull(evaluationReport, "Evaluation report isn't specified!");
        this.evaluationReport = evaluationReport;
    }

    /**
     * Sets decimal format.
     *
     * @param decimalFormat - decimal format
     */
    public void setDecimalFormat(DecimalFormat decimalFormat) {
        Objects.requireNonNull(decimalFormat, "Decimal format isn't specified!");
        this.decimalFormat = decimalFormat;
    }

    /**
     * Saves report.
     */
    public abstract void saveReport() throws Exception;

    /**
     * Validates specified file.
     *
     * @param file - file
     */
    protected abstract void validateFile(File file);
}
