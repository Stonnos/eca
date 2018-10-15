package eca.report;

import eca.config.VelocityConfigService;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Implements contingency table report.
 *
 * @author Roman Batygin
 */
public class ContingencyTableReportService implements ReportService {

    /**
     * Velocity configuration
     */
    private static final VelocityConfigService VELOCITY_CONFIGURATION =
            VelocityConfigService.getVelocityConfigService();

    private static final String VM_REPORT_TEMPLATE = "vm-templates/contingencyTableReport.vm";
    private static final String CP1251 = "cp1251";
    private static final String HTML_EXTENSION = ".html";

    private static final String CHI_SQUARE_RESULT_PARAM = "chiSquareResult";

    /**
     * Target file
     */
    @Getter
    private File file;

    /**
     * Decimal format
     */
    @Getter
    private DecimalFormat decimalFormat;

    /**
     * Contingency table report model
     */
    @Getter
    private ContingencyTableReportModel contingencyTableReportModel;

    /**
     * Sets file for saving report.
     *
     * @param file - file
     */
    public void setFile(File file) {
        Objects.requireNonNull(file, "file isn't specified!");
        if (!file.getName().endsWith(HTML_EXTENSION)) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
        this.file = file;
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
     * Sets contingency table report model.
     *
     * @param contingencyTableReportModel - contingency table report model
     */
    public void setContingencyTableReportModel(ContingencyTableReportModel contingencyTableReportModel) {
        Objects.requireNonNull(contingencyTableReportModel, "Contingency table report isn't specified!");
        Objects.requireNonNull(contingencyTableReportModel.getRowAttribute(), "Row attribute isn't specified!");
        Objects.requireNonNull(contingencyTableReportModel.getColAttribute(), "Column attribute isn't specified!");
        Objects.requireNonNull(contingencyTableReportModel.getContingencyMatrix(),
                "Contingency matrix isn't specified!");
        Objects.requireNonNull(contingencyTableReportModel.getChiValueResult(), "Chi square result isn't specified!");
        this.contingencyTableReportModel = contingencyTableReportModel;
    }

    @Override
    public void saveReport() throws IOException {
        Template template = VELOCITY_CONFIGURATION.getTemplate(VM_REPORT_TEMPLATE);
        VelocityContext context = new VelocityContext();
        context.put(CHI_SQUARE_RESULT_PARAM,
                ReportGenerator.getChiSquareTestResultAsHtml(contingencyTableReportModel.getChiValueResult(),
                        decimalFormat));
        String htmlString = mergeContext(template, context);
        FileUtils.write(getFile(), htmlString, Charset.forName(CP1251));
    }

    private String mergeContext(Template template, VelocityContext context) {
        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);
        return stringWriter.toString();
    }
}
