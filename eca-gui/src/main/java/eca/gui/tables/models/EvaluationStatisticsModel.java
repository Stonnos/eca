package eca.gui.tables.models;

import eca.core.evaluation.Evaluation;
import eca.text.NumericFormatFactory;
import eca.util.Entry;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.Classifier;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static eca.gui.dictionary.EvaluationStatisticsDictionary.CLASSIFIER_ACCURACY_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.CLASSIFIER_ERROR_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.CLASSIFIER_MEAN_ERROR_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.CLASSIFIER_NAME_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.CORRECTLY_CLASSIFIED_INSTANCES_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.CROSS_VALIDATION_METHOD_FORMAT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.ERROR_CONFIDENCE_INTERVAL_ERROR_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.EVALUATION_METHOD_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.INCORRECTLY_CLASSIFIED_INSTANCES_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.INITIAL_DATA_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.INTERVAL_FORMAT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.NUMBER_OF_ATTRIBUTES_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.NUMBER_OF_CLASSES_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.NUMBER_OF_INSTANCES_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.NUMBER_OF_TEST_INSTANCES;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.ROOT_MEAN_SQUARED_ERROR_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.TOTAL_TIME_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.TRAINING_DATA_METHOD_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.VARIANCE_ERROR_TEXT;

/**
 * @author Roman Batygin
 */
public class EvaluationStatisticsModel extends AbstractTableModel {

    private static final String DATE_TIME_FORMAT = "HH:mm:ss:SSS";
    private static final String TIMEZONE = "GMT";

    private static final String[] TITLE = {"Статистика", "Значение"};

    @Getter
    private final List<Entry<String, String>> results = new ArrayList<>();

    private final DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    private SimpleDateFormat dateFormat;

    public EvaluationStatisticsModel(Evaluation evaluation, Classifier classifier, int maxFractionDigits) {
        this.decimalFormat.setMaximumFractionDigits(maxFractionDigits);
        this.dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        this.populateEvaluationStatistics(evaluation, classifier);
    }

    @Override
    public int getColumnCount() {
        return TITLE.length;
    }

    @Override
    public int getRowCount() {
        return results.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return column == 0 ? results.get(row).getKey()
                : results.get(row).getValue();
    }

    public void addRow(Entry<String, String> value) {
        results.add(value);
        fireTableRowsInserted(getRowCount(), getRowCount());
    }

    @Override
    public String getColumnName(int column) {
        return TITLE[column];
    }

    private void populateEvaluationStatistics(Evaluation evaluation, Classifier classifier) {
        results.add(new Entry<>(INITIAL_DATA_TEXT, evaluation.getHeader().relationName()));
        results.add(
                new Entry<>(NUMBER_OF_INSTANCES_TEXT, decimalFormat.format(evaluation.getData().numInstances())));
        results.add(
                new Entry<>(NUMBER_OF_ATTRIBUTES_TEXT, decimalFormat.format(evaluation.getData().numAttributes())));
        results.add(new Entry<>(NUMBER_OF_CLASSES_TEXT, decimalFormat.format(evaluation.getData().numClasses())));
        results.add(new Entry<>(CLASSIFIER_NAME_TEXT, classifier.getClass().getSimpleName()));

        String evaluationMethodStr;
        if (evaluation.isKCrossValidationMethod()) {
            evaluationMethodStr = String.format(CROSS_VALIDATION_METHOD_FORMAT,
                    (evaluation.getValidationsNum() > 1 ? evaluation.getValidationsNum() + "*" : StringUtils.EMPTY),
                    evaluation.numFolds());
        } else {
            evaluationMethodStr = TRAINING_DATA_METHOD_TEXT;
        }

        results.add(new Entry<>(EVALUATION_METHOD_TEXT, evaluationMethodStr));
        results.add(new Entry<>(NUMBER_OF_TEST_INSTANCES, decimalFormat.format(evaluation.numInstances())));
        results.add(new Entry<>(CORRECTLY_CLASSIFIED_INSTANCES_TEXT, decimalFormat.format(evaluation.correct())));
        results.add(
                new Entry<>(INCORRECTLY_CLASSIFIED_INSTANCES_TEXT, decimalFormat.format(evaluation.incorrect())));
        results.add(new Entry<>(CLASSIFIER_ACCURACY_TEXT, decimalFormat.format(evaluation.pctCorrect())));
        results.add(new Entry<>(CLASSIFIER_ERROR_TEXT, decimalFormat.format(evaluation.pctIncorrect())));
        results.add(new Entry<>(CLASSIFIER_MEAN_ERROR_TEXT, decimalFormat.format(evaluation.meanAbsoluteError())));
        results.add(
                new Entry<>(ROOT_MEAN_SQUARED_ERROR_TEXT, decimalFormat.format(evaluation.rootMeanSquaredError())));
        if (evaluation.isKCrossValidationMethod()) {
            results.add(new Entry<>(VARIANCE_ERROR_TEXT, decimalFormat.format(evaluation.varianceError())));
            double[] x = evaluation.errorConfidenceInterval();
            results.add(new Entry<>(ERROR_CONFIDENCE_INTERVAL_ERROR_TEXT,
                    String.format(INTERVAL_FORMAT, decimalFormat.format(x[0]), decimalFormat.format(x[1]))));
        }
        if (evaluation.getTotalTimeMillis() != null) {
            results.add(new Entry<>(TOTAL_TIME_TEXT, dateFormat.format(new Date(evaluation.getTotalTimeMillis()))));
        }
    }
}
