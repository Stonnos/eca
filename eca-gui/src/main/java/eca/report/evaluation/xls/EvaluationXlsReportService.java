package eca.report.evaluation.xls;

import com.google.common.collect.ImmutableList;
import eca.core.evaluation.Evaluation;
import eca.data.DataFileExtension;
import eca.data.FileUtils;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.StackingClassifier;
import eca.report.evaluation.AbstractEvaluationReportService;
import eca.report.model.AttachmentImage;
import eca.report.model.DecisionTreeReport;
import eca.report.model.EvaluationReport;
import eca.report.model.LogisticReport;
import eca.report.model.NeuralNetworkReport;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.List;

import static eca.util.ClassifierNamesFactory.getClassifierName;

/**
 * Implements classification results saving into xls, xlsx file.
 *
 * @author Roman Batygin
 */
public class EvaluationXlsReportService extends AbstractEvaluationReportService {

    private static final String RESULTS_TEXT = "Результаты классификации";
    private static final String STATISTICS_TEXT = "Статистика";
    private static final String MATRIX_TEXT = "Матрица классификации";
    private static final String INPUT_OPTIONS_TEXT = "Входные параметры";
    private static final String INDIVIDUAL_CLASSIFIER_INPUT_OPTIONS = "Входные параметры классификатора";
    private static final String INDIVIDUAL_CLASSIFIER = "Классификатор";
    private static final String CLASSIFIERS_INPUT_OPTIONS = "Входные параметры базовых классификаторов:";
    private static final String META_CLASSIFIER_INPUT_OPTIONS = "Входные параметры мета-классификатора";
    private static final String META_CLASSIFIER_TEXT = "Мета-классификатор:";
    private static final String INDIVIDUAL_CLASSIFIER_TEXT = "Базовый классификатор:";
    private static final String PNG = "PNG";
    private static final int FONT_SIZE = 12;
    private static final int PICTURE_ANCHOR_COL1 = 5;
    private static final int PICTURE_ANCHOR_ROW1 = 5;
    private static final String ACTUAL_CLASS_TEXT = "Реальное";
    private static final String PREDICTED_VALUE_FORMAT = "%s (Прогнозное)";
    private static final String[] COST_CLASSIFICATION_HEADER =
            {"Класс", "TPR", "FPR", "TNR", "FNR", "Полнота", "Точность", "F - мера", "AUC"};
    private static final String NAN = "NaN";
    private static final String DOUBLE_FORMAT = "^[-]?[0-9]*[,]?[0-9]*$";

    private static final int CLASS_COL_IDX = 0;
    private static final int TP_COL_IDX = 1;
    private static final int FP_COL_IDX = 2;
    private static final int TN_COL_IDX = 3;
    private static final int FN_COL_IDX = 4;
    private static final int RECALL_COL_IDX = 5;
    private static final int PRECISION_COL_IDX = 6;
    private static final int FM_COL_IDX = 7;
    private static final int AUC_COL_IDX = 8;

    private List<EvaluationReportHandler> evaluationReportHandlers =
            ImmutableList.of(new DecisionTreeReportHandler(), new NeuralNetworkReportHandler(),
                    new LogisticReportHandler());

    @Override
    public void saveReport() throws Exception {
        try (FileOutputStream stream = new FileOutputStream(getFile()); Workbook book = createWorkbook(getFile())) {
            populateReportData(book);
            book.write(stream);
        }
    }

    @Override
    protected void validateFile(File file) {
        if (!FileUtils.isXlsExtension(file.getName())) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }

    private Workbook createWorkbook(File file) {
        return file.getName().endsWith(DataFileExtension.XLS.getExtendedExtension()) ? new HSSFWorkbook() :
                new XSSFWorkbook();
    }

    private Font createTitleFont(Workbook book) {
        Font font = book.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) FONT_SIZE);
        return font;
    }

    private void populateRocCurveImage(Workbook book) throws Exception {
        AttachmentImage rocCurveImage = getEvaluationReport().getRocCurveImage();
        writeImage(book, (BufferedImage) rocCurveImage.getImage(), rocCurveImage.getTitle());
    }

    private void populateReportData(Workbook book) throws Exception {
        Font font = createTitleFont(book);
        CellStyle style = book.createCellStyle();
        style.setFont(font);
        createClassifierInputParamSheet(book, style);
        createXlsResultsSheet(book, style);
        populateRocCurveImage(book);
        populateAdditionalReportData(book);
    }

    private void populateAdditionalReportData(Workbook workbook) throws Exception {
        EvaluationReportHandler evaluationReportHandler = evaluationReportHandlers.stream()
                .filter(h -> h.canHandle(getEvaluationReport()))
                .findFirst()
                .orElse(null);
        if (evaluationReportHandler != null) {
            evaluationReportHandler.populateReport(workbook, getEvaluationReport());
        }
    }

    private void createClassifierInputParamSheet(Workbook book, CellStyle style) {
        Sheet sheet = book.createSheet(INPUT_OPTIONS_TEXT);
        AbstractClassifier classifier = (AbstractClassifier) getEvaluationReport().getClassifier();
        createTitle(sheet, style, INDIVIDUAL_CLASSIFIER_INPUT_OPTIONS);
        createPair(sheet, style, INDIVIDUAL_CLASSIFIER, getClassifierName(classifier));
        String[] inputOptions = classifier.getOptions();
        setXlsClassifierOptions(sheet, inputOptions);
        if (classifier instanceof AbstractHeterogeneousClassifier) {
            createTitle(sheet, style, CLASSIFIERS_INPUT_OPTIONS);
            AbstractHeterogeneousClassifier heterogeneousClassifier = (AbstractHeterogeneousClassifier) classifier;
            ClassifiersSet classifiersSet = heterogeneousClassifier.getClassifiersSet();
            setXlsEnsembleOptions(sheet, style, classifiersSet.toList());
        } else if (classifier instanceof StackingClassifier) {
            createTitle(sheet, style, CLASSIFIERS_INPUT_OPTIONS);
            StackingClassifier stackingClassifier = (StackingClassifier) classifier;
            setXlsEnsembleOptions(sheet, style, stackingClassifier.getClassifiers().toList());
            createTitle(sheet, style, META_CLASSIFIER_INPUT_OPTIONS);
            String[] metaClassifierOptions = ((AbstractClassifier) stackingClassifier.getMetaClassifier()).getOptions();
            createPair(sheet, style, META_CLASSIFIER_TEXT, getClassifierName(stackingClassifier));
            setXlsClassifierOptions(sheet, metaClassifierOptions);
        }
    }

    private void setXlsClassifierOptions(Sheet sheet, String[] options) {
        for (int i = 0; i < options.length; i += 2) {
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell cell = row.createCell(0);
            cell.setCellValue(options[i]);
            sheet.autoSizeColumn(0);
            cell = row.createCell(1);
            setCellValue(cell, options[i + 1]);
            sheet.autoSizeColumn(1);
        }
    }

    private void setXlsEnsembleOptions(Sheet sheet, CellStyle style, List<Classifier> classifiers) {
        classifiers.forEach(classifier -> {
            AbstractClassifier single = (AbstractClassifier) classifier;
            createPair(sheet, style, INDIVIDUAL_CLASSIFIER_TEXT, getClassifierName(single));
            createTitle(sheet, style, INPUT_OPTIONS_TEXT);
            String[] singleOptions = single.getOptions();
            setXlsClassifierOptions(sheet, singleOptions);
        });
    }

    private void createTitle(Sheet sheet, CellStyle style, String title) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(title);
        sheet.autoSizeColumn(0);
    }

    private void createPair(Sheet sheet, CellStyle style, String title1, String title2) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(title1);
        sheet.autoSizeColumn(0);
        cell = row.createCell(1);
        cell.setCellStyle(style);
        cell.setCellValue(title2);
        sheet.autoSizeColumn(1);
    }

    private void setCellValue(Cell cell, String value) {
        try {
            if (value.matches(DOUBLE_FORMAT)) {
                cell.setCellValue(getDecimalFormat().parse(value).doubleValue());
            } else {
                cell.setCellValue(value);
            }
        } catch (ParseException ex) {
            throw new IllegalArgumentException(String.format("Can't set cell value for %s!", value));
        }
    }

    private CellStyle createBorderedCellStyle(Workbook book) {
        CellStyle cellStyle = book.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }

    private CellStyle createHeaderStyle(Workbook book) {
        CellStyle style = createBorderedCellStyle(book);
        Font font = book.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void createXlsResultsSheet(Workbook book, CellStyle style) {
        Sheet sheet = book.createSheet(RESULTS_TEXT);
        CellStyle tableStyle = createBorderedCellStyle(book);
        CellStyle headerStyle = createHeaderStyle(book);
        createStatisticsTable(sheet, style, tableStyle);
        createCostMatrix(sheet, style, tableStyle, headerStyle);
        createMisClassificationMatrix(sheet, style, tableStyle, headerStyle);
    }

    private void createStatisticsTable(Sheet sheet, CellStyle style, CellStyle tableStyle) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(STATISTICS_TEXT);
        getEvaluationReport().getStatisticsMap().forEach((key, value) -> {
            Row statisticsRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell statisticsCell = statisticsRow.createCell(0);
            statisticsCell.setCellStyle(tableStyle);
            statisticsCell.setCellValue(key);
            statisticsCell = statisticsRow.createCell(1);
            statisticsCell.setCellStyle(tableStyle);
            setCellValue(statisticsCell, value);
        });
    }

    private void createMisClassificationMatrix(Sheet sheet, CellStyle titleStyle, CellStyle tableStyle,
                                               CellStyle headerStyle) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell cell = row.createCell(0);
        cell.setCellStyle(titleStyle);
        cell.setCellValue(MATRIX_TEXT);
        row = sheet.createRow(sheet.getPhysicalNumberOfRows());

        //Creates table header
        cell = row.createCell(0);
        cell.setCellStyle(headerStyle);
        cell.setCellValue(ACTUAL_CLASS_TEXT);
        sheet.autoSizeColumn(0);
        Attribute classAttribute = getEvaluationReport().getData().classAttribute();
        for (int i = 1; i <= classAttribute.numValues(); i++) {
            cell = row.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(String.format(PREDICTED_VALUE_FORMAT, classAttribute.value(i - 1)));
            sheet.autoSizeColumn(i);
        }

        //Creates confusion classification table
        double[][] confusionMatrix = getEvaluationReport().getEvaluation().confusionMatrix();
        for (int i = 0; i < confusionMatrix.length; i++) {
            row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            cell = row.createCell(0);
            cell.setCellStyle(tableStyle);
            cell.setCellValue(classAttribute.value(i));
            for (int j = 1; j <= confusionMatrix[i].length; j++) {
                cell = row.createCell(j);
                cell.setCellStyle(tableStyle);
                cell.setCellValue(confusionMatrix[i][j - 1]);
            }
        }
    }

    private void createCostMatrix(Sheet sheet, CellStyle titleStyle, CellStyle tableStyle, CellStyle headerStyle) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        Cell cell = row.createCell(0);
        cell.setCellStyle(titleStyle);
        cell.setCellValue(RESULTS_TEXT);
        row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        //Creates table header
        for (int i = 0; i < COST_CLASSIFICATION_HEADER.length; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(COST_CLASSIFICATION_HEADER[i]);
        }
        //Creates results table
        Attribute classAttribute = getEvaluationReport().getData().classAttribute();
        for (int i = 0; i < classAttribute.numValues(); i++) {
            row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            for (int j = 0; j < COST_CLASSIFICATION_HEADER.length; j++) {
                cell = row.createCell(j);
                cell.setCellStyle(tableStyle);
                setEvaluationResultsValue(cell, j, i);
            }
        }
    }

    private void setEvaluationResultsValue(Cell cell, int column, int classIndex) {
        Attribute classAttribute = getEvaluationReport().getData().classAttribute();
        Evaluation evaluation = getEvaluationReport().getEvaluation();
        switch (column) {
            case CLASS_COL_IDX:
                cell.setCellValue(classAttribute.value(classIndex));
                break;
            case TP_COL_IDX:
                setCellValue(cell, getDecimalFormat().format(evaluation.truePositiveRate(classIndex)));
                break;
            case FP_COL_IDX:
                setCellValue(cell, getDecimalFormat().format(evaluation.falsePositiveRate(classIndex)));
                break;
            case TN_COL_IDX:
                setCellValue(cell, getDecimalFormat().format(evaluation.trueNegativeRate(classIndex)));
                break;
            case FN_COL_IDX:
                setCellValue(cell, getDecimalFormat().format(evaluation.falseNegativeRate(classIndex)));
                break;
            case RECALL_COL_IDX:
                setCellValue(cell, getDecimalFormat().format(evaluation.recall(classIndex)));
                break;
            case PRECISION_COL_IDX:
                setCellValue(cell, getDecimalFormat().format(evaluation.precision(classIndex)));
                break;
            case FM_COL_IDX:
                setCellValue(cell, getDecimalFormat().format(evaluation.fMeasure(classIndex)));
                break;
            case AUC_COL_IDX:
                double aucValue = evaluation.areaUnderROC(classIndex);
                setCellValue(cell, Double.isNaN(aucValue) ? NAN : getDecimalFormat().format(aucValue));
                break;
            default:
                throw new IllegalStateException("Unexpected column index!");
        }
    }

    private void writeImage(Workbook book, BufferedImage image, String title) throws Exception {
        Sheet sheet = book.createSheet(title);
        @Cleanup ByteArrayOutputStream byteArrayImg = new ByteArrayOutputStream();
        ImageIO.write(image, PNG, byteArrayImg);
        int pictureIdx = sheet.getWorkbook().addPicture(byteArrayImg.toByteArray(), Workbook.PICTURE_TYPE_PNG);
        CreationHelper factory = book.getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(PICTURE_ANCHOR_COL1);
        anchor.setRow1(PICTURE_ANCHOR_ROW1);
        Drawing drawing = sheet.createDrawingPatriarch();
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize();
    }

    /**
     * Evaluation report handler.
     *
     * @param <T> - evaluation report generic type
     */
    @RequiredArgsConstructor
    private abstract class EvaluationReportHandler<T extends EvaluationReport> {

        private final Class<T> reportClazz;

        boolean canHandle(T report) {
            return reportClazz.isAssignableFrom(report.getClass());
        }

        abstract void populateReport(Workbook workbook, T report) throws Exception;
    }

    /**
     * Decision tree report handler.
     */
    private class DecisionTreeReportHandler extends EvaluationReportHandler<DecisionTreeReport> {

        DecisionTreeReportHandler() {
            super(DecisionTreeReport.class);
        }

        @Override
        void populateReport(Workbook workbook, DecisionTreeReport report) throws Exception {
            AttachmentImage treeImage = report.getTreeImage();
            writeImage(workbook, (BufferedImage) treeImage.getImage(), treeImage.getTitle());
        }
    }

    /**
     * Neural network report handler.
     */
    private class NeuralNetworkReportHandler extends EvaluationReportHandler<NeuralNetworkReport> {

        NeuralNetworkReportHandler() {
            super(NeuralNetworkReport.class);
        }

        @Override
        void populateReport(Workbook workbook, NeuralNetworkReport report) throws Exception {
            AttachmentImage networkImage = report.getNetworkImage();
            writeImage(workbook, (BufferedImage) networkImage.getImage(), networkImage.getTitle());
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
            super(LogisticReport.class);
        }

        @Override
        void populateReport(Workbook workbook, LogisticReport report) throws Exception {
            Sheet sheet = workbook.createSheet(report.getLogisticCoefficientsModel().getTitle());
            populateCoefficients(workbook, sheet, report);
        }

        void populateCoefficients(Workbook book, Sheet sheet, LogisticReport report) {
            CellStyle tableStyle = createBorderedCellStyle(book);
            populateCoefficientsHeader(book, sheet, report);
            Attribute classAttribute = report.getData().classAttribute();
            double[][] coefficients = report.getLogisticCoefficientsModel().getCoefficients();
            for (int i = 0; i < coefficients.length; i++) {
                Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                Cell cell = row.createCell(0);
                cell.setCellStyle(tableStyle);
                cell.setCellValue(getAttributeName(i, report.getLogisticCoefficientsModel().getMeta()));
                sheet.autoSizeColumn(0);
                for (int j = 0; j < classAttribute.numValues() - 1; j++) {
                    int cellIdx = j + 1;
                    cell = row.createCell(cellIdx);
                    cell.setCellStyle(tableStyle);
                    setCellValue(cell, getDecimalFormat().format(coefficients[i][j]));
                    sheet.autoSizeColumn(cellIdx);
                }
            }
        }

        void populateCoefficientsHeader(Workbook book, Sheet sheet, LogisticReport report) {
            CellStyle headerStyle = createHeaderStyle(book);
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            Cell cell = row.createCell(0);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(ATTR_TEXT);
            sheet.autoSizeColumn(0);
            Attribute classAttribute = report.getData().classAttribute();
            for (int i = 1; i < classAttribute.numValues(); i++) {
                cell = row.createCell(i);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(String.format(CLASS_FORMAT, i - 1));
                sheet.autoSizeColumn(i);
            }
        }

        String getAttributeName(int attrIndex, Instances data) {
            return attrIndex == 0 ? INTERCEPT : data.attribute(attrIndex - 1).name();
        }
    }
}
