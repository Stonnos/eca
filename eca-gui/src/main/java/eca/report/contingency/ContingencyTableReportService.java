package eca.report.contingency;

import eca.config.VelocityConfigService;
import eca.report.ReportGenerator;
import eca.report.ReportService;
import eca.util.Utils;
import eca.util.VelocityUtils;
import lombok.Getter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import weka.core.Attribute;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implements contingency table report.
 *
 * @author Roman Batygin
 */
public class ContingencyTableReportService implements ReportService {

    private static final String VM_REPORT_TEMPLATE = "vm-templates/contingencyTableReport.vm";
    private static final String HTML_EXTENSION = ".html";

    private static final String SUMMARY_TEXT = "Всего";

    private static final String ROW_ATTR_NAME_PARAM = "rowAttrName";
    private static final String COL_ATTR_NAME_PARAM = "colAttrName";
    private static final String COL_ATTR_VALUES_PARAM = "colAttrValues";
    private static final String CONTINGENCY_MATRIX_PARAM = "contingencyMatrix";
    private static final String CHI_SQUARE_RESULT_PARAM = "chiSquareResult";

    /**
     * Target file
     */
    @Getter
    private File file;

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
        Objects.requireNonNull(file, "File isn't specified!");
        if (!file.getName().endsWith(HTML_EXTENSION)) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
        this.file = file;
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
        Objects.requireNonNull(contingencyTableReportModel.getChiSquareTestResult(),
                "Chi square result isn't specified!");
        Objects.requireNonNull(contingencyTableReportModel.getDecimalFormat(), "Decimal format isn't specified!");
        this.contingencyTableReportModel = contingencyTableReportModel;
    }

    @Override
    public void saveReport() throws IOException {
        Template template = VelocityConfigService.getTemplate(VM_REPORT_TEMPLATE);
        VelocityContext context = new VelocityContext();
        fillContingencyTable(context);
        context.put(CHI_SQUARE_RESULT_PARAM, ReportGenerator.getChiSquareTestResultAsHtml(contingencyTableReportModel));
        VelocityUtils.mergeAndWrite(getFile(), template, context);
    }

    private void fillContingencyTable(VelocityContext context) {
        context.put(ROW_ATTR_NAME_PARAM, contingencyTableReportModel.getRowAttribute().name());
        context.put(COL_ATTR_NAME_PARAM, contingencyTableReportModel.getColAttribute().name());
        context.put(COL_ATTR_VALUES_PARAM, Utils.getAttributeValues(contingencyTableReportModel.getColAttribute()));
        context.put(CONTINGENCY_MATRIX_PARAM, createContingencyTableList());
    }

    private List<List<String>> createContingencyTableList() {
        double[][] contingencyTable = contingencyTableReportModel.getContingencyMatrix();
        Attribute rowAttribute = contingencyTableReportModel.getRowAttribute();
        List<List<String>> contingencyTableList = new ArrayList<>(contingencyTable.length);
        for (int i = 0; i < contingencyTable.length; i++) {
            List<String> row = new ArrayList<>(contingencyTable[i].length);
            row.add(i < contingencyTable.length - 1 ? rowAttribute.value(i) : SUMMARY_TEXT);
            for (int j = 0; j < contingencyTable[i].length; j++) {
                row.add(String.valueOf((int) contingencyTable[i][j]));
            }
            contingencyTableList.add(row);
        }
        return contingencyTableList;
    }
}
