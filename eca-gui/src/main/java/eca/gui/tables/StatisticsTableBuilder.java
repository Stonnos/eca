/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.core.evaluation.Evaluation;
import eca.ensemble.IterativeEnsembleClassifier;
import eca.ensemble.StackingClassifier;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.text.NumericFormatFactory;
import eca.trees.DecisionTreeClassifier;
import eca.trees.J48;
import eca.util.Entry;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.Classifier;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Implements building classifiers evaluation results represented in table.
 *
 * @author Roman Batygin
 */
public class StatisticsTableBuilder {

    private static final String[] TITLE = {"Статистика", "Значение"};
    public static final String INITIAL_DATA_TEXT = "Исходные данные";
    public static final String NUMBER_OF_INSTANCES_TEXT = "Число объектов";
    public static final String NUMBER_OF_ATTRIBUTES_TEXT = "Число атрибутов";
    public static final String NUMBER_OF_CLASSES_TEXT = "Число классов";
    public static final String CLASSIFIER_NAME_TEXT = "Классификатор";
    public static final String EVALUATION_METHOD_TEXT = "Метод оценки точности";
    public static final String TRAINING_DATA_METHOD_TEXT = "Использование обучающей выборки";
    public static final String NUMBER_OF_TEST_INSTANCES = "Число объектов тестовых данных";
    public static final String CORRECTLY_CLASSIFIED_INSTANCES_TEXT = "Число правильно классифицированных объектов";
    public static final String INCORRECTLY_CLASSIFIED_INSTANCES_TEXT = "Число неправильно классифицированных объектов";
    public static final String CLASSIFIER_ACCURACY_TEXT = "Точность классификатора, %";
    public static final String CLASSIFIER_ERROR_TEXT = "Ошибка классификатора, %";
    public static final String CLASSIFIER_MEAN_ERROR_TEXT = "Средняя абсолютная ошибка классификации";
    public static final String ROOT_MEAN_SQUARED_ERROR_TEXT = "Среднеквадратическая ошибка классификации";
    public static final String VARIANCE_ERROR_TEXT = "Дисперсия ошибки классификатора";
    public static final String ERROR_CONFIDENCE_INTERVAL_ERROR_TEXT =
            "95% доверительный интервал ошибки классификатора";
    public static final String NUMBER_OF_NODES_TEXT = "Число узлов";
    public static final String NUMBER_OF_LEAVES_TEXT = "Число листьев";
    public static final String TREE_DEPTH_TEXT = "Глубина дерева";
    public static final String CLASSIFIERS_IN_ENSEMBLE_TEXT = "Число классификаторов в ансамбле";
    public static final String DISTANCE_FUNCTION_TEXT = "Функция расстояния";
    public static final String META_CLASSIFIER_NAME_TEXT = "Мета-классификатор";
    public static final String IN_LAYER_NEURONS_NUM_TEXT = "Число нейронов во входном слое";
    public static final String OUT_LAYER_NEURONS_NUM_TEXT = "Число нейронов в выходном слое";
    public static final String HIDDEN_LAYERS_NUM_TEXT = "Число скрытых слоев";
    public static final String HIDDEN_LAYER_STRUCTURE_TEXT = "Структура скрытого слоя";
    public static final String LINKS_NUM_TEXT = "Число связей";
    public static final String AF_OF_HIDDEN_LAYER_TEXT = "Активационная функция нейронов скрытого слоя";
    public static final String AF_OF_OUT_LAYER_TEXT = "Активационная функция нейронов выходного слоя";
    public static final String LEARNING_ALGORITHM_TEXT = "Алгоритм обучения";
    public static final String CROSS_VALIDATION_METHOD_FORMAT = "Кросс - проверка, %s%d - блочная";
    public static final String TOTAL_TIME_TEXT = "Затраченное время";

    private final DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    private static final SimpleDateFormat DATE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Evaluation statistics model.
     */
    private class ResultsModel extends AbstractTableModel {

        ArrayList<Entry<String, String>> results = new ArrayList<>();

        ResultsModel(Evaluation e, Classifier classifier) {
            results.add(new Entry<>(INITIAL_DATA_TEXT, e.getHeader().relationName()));
            results.add(new Entry<>(NUMBER_OF_INSTANCES_TEXT, decimalFormat.format(e.getData().numInstances())));
            results.add(new Entry<>(NUMBER_OF_ATTRIBUTES_TEXT, decimalFormat.format(e.getData().numAttributes())));
            results.add(new Entry<>(NUMBER_OF_CLASSES_TEXT, decimalFormat.format(e.getData().numClasses())));
            results.add(new Entry<>(CLASSIFIER_NAME_TEXT, classifier.getClass().getSimpleName()));

            String evaluationMethodStr;
            if (e.isKCrossValidationMethod()) {
                evaluationMethodStr = String.format(CROSS_VALIDATION_METHOD_FORMAT,
                        (e.getValidationsNum() > 1 ? e.getValidationsNum() + "*" : StringUtils.EMPTY), e.numFolds());
            } else {
                evaluationMethodStr = TRAINING_DATA_METHOD_TEXT;
            }

            results.add(new Entry<>(EVALUATION_METHOD_TEXT, evaluationMethodStr));
            results.add(new Entry<>(NUMBER_OF_TEST_INSTANCES, decimalFormat.format(e.numInstances())));
            results.add(new Entry<>(CORRECTLY_CLASSIFIED_INSTANCES_TEXT, decimalFormat.format(e.correct())));
            results.add(new Entry<>(INCORRECTLY_CLASSIFIED_INSTANCES_TEXT, decimalFormat.format(e.incorrect())));
            results.add(new Entry<>(CLASSIFIER_ACCURACY_TEXT, decimalFormat.format(e.pctCorrect())));
            results.add(new Entry<>(CLASSIFIER_ERROR_TEXT, decimalFormat.format(e.pctIncorrect())));
            results.add(new Entry<>(CLASSIFIER_MEAN_ERROR_TEXT, decimalFormat.format(e.meanAbsoluteError())));
            results.add(new Entry<>(ROOT_MEAN_SQUARED_ERROR_TEXT, decimalFormat.format(e.rootMeanSquaredError())));
            if (e.isKCrossValidationMethod()) {
                results.add(new Entry<>(VARIANCE_ERROR_TEXT, decimalFormat.format(e.varianceError())));
                double[] x = e.errorConfidenceInterval();
                results.add(new Entry<>(ERROR_CONFIDENCE_INTERVAL_ERROR_TEXT,
                        String.format("[%s; %s]", decimalFormat.format(x[0]), decimalFormat.format(x[1]))));
            }
            if (e.getTotalTimeMillis() != null) {
                results.add(new Entry<>(TOTAL_TIME_TEXT, DATE_FORMAT.format(new Date(e.getTotalTimeMillis()))));
            }
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
    }

    public StatisticsTableBuilder(int digits) {
        decimalFormat.setMaximumFractionDigits(digits);
    }

    public final JTable createStatistics(DecisionTreeClassifier tree, Evaluation evaluation) {
        ResultsModel model = new ResultsModel(evaluation, tree);
        model.addRow(new Entry<>(NUMBER_OF_NODES_TEXT, String.valueOf(tree.numNodes())));
        model.addRow(new Entry<>(NUMBER_OF_LEAVES_TEXT, String.valueOf(tree.numLeaves())));
        model.addRow(new Entry<>(TREE_DEPTH_TEXT, String.valueOf(tree.depth())));
        return createTable(model);
    }

    public final JTable createStatistics(NeuralNetwork mlp, Evaluation evaluation) {
        ResultsModel model = new ResultsModel(evaluation, mlp);
        model.addRow(new Entry<>(IN_LAYER_NEURONS_NUM_TEXT,
                String.valueOf(mlp.getMultilayerPerceptron().getNumInNeurons())));
        model.addRow(new Entry<>(OUT_LAYER_NEURONS_NUM_TEXT,
                String.valueOf(mlp.getMultilayerPerceptron().getNumOutNeurons())));
        model.addRow(
                new Entry<>(HIDDEN_LAYERS_NUM_TEXT, String.valueOf(mlp.getMultilayerPerceptron().hiddenLayersNum())));
        model.addRow(new Entry<>(HIDDEN_LAYER_STRUCTURE_TEXT, mlp.getMultilayerPerceptron().getHiddenLayer()));
        model.addRow(new Entry<>(LINKS_NUM_TEXT, String.valueOf(mlp.getMultilayerPerceptron().getLinksNum())));
        model.addRow(new Entry<>(AF_OF_HIDDEN_LAYER_TEXT, mlp.getMultilayerPerceptron().getActivationFunction()
                .getActivationFunctionType().getDescription()));
        model.addRow(new Entry<>(AF_OF_OUT_LAYER_TEXT, mlp.getMultilayerPerceptron()
                .getOutActivationFunction().getActivationFunctionType().getDescription()));
        model.addRow(
                new Entry<>(LEARNING_ALGORITHM_TEXT,
                        mlp.getMultilayerPerceptron().getLearningAlgorithm().getClass().getSimpleName()));
        return createTable(model);
    }

    public final JTable createStatistics(IterativeEnsembleClassifier cls, Evaluation evaluation) {
        ResultsModel model = new ResultsModel(evaluation, cls);
        model.addRow(new Entry<>(CLASSIFIERS_IN_ENSEMBLE_TEXT, String.valueOf(cls.numClassifiers())));
        return createTable(model);
    }

    public final JTable createStatistics(Logistic cls, Evaluation evaluation) {
        return createTable(new ResultsModel(evaluation, cls));
    }

    public final JTable createStatistics(KNearestNeighbours cls, Evaluation evaluation) {
        ResultsModel model = new ResultsModel(evaluation, cls);
        model.addRow(new Entry<>(DISTANCE_FUNCTION_TEXT, cls.getDistance().getDistanceType().getDescription()));
        return createTable(model);
    }

    public final JTable createStatistics(StackingClassifier cls, Evaluation evaluation) {
        ResultsModel model = new ResultsModel(evaluation, cls);
        model.addRow(new Entry<>(CLASSIFIERS_IN_ENSEMBLE_TEXT, String.valueOf(cls.numClassifiers())));
        model.addRow(new Entry<>(META_CLASSIFIER_NAME_TEXT, cls.getMetaClassifier().getClass().getSimpleName()));
        return createTable(model);
    }

    public final JTable createStatistics(J48 j48, Evaluation evaluation) {
        ResultsModel model = new ResultsModel(evaluation, j48);
        model.addRow(new Entry<>(NUMBER_OF_NODES_TEXT, String.valueOf((int) j48.measureTreeSize())));
        model.addRow(new Entry<>(NUMBER_OF_LEAVES_TEXT, String.valueOf((int) j48.measureNumLeaves())));
        return createTable(model);
    }

    public final JTable createStatistics(Classifier cls, Evaluation evaluation) {
        if (cls instanceof IterativeEnsembleClassifier) {
            return createStatistics((IterativeEnsembleClassifier) cls, evaluation);
        } else if (cls instanceof NeuralNetwork) {
            return createStatistics((NeuralNetwork) cls, evaluation);
        } else if (cls instanceof DecisionTreeClassifier) {
            return createStatistics((DecisionTreeClassifier) cls, evaluation);
        } else if (cls instanceof Logistic) {
            return createStatistics((Logistic) cls, evaluation);
        } else if (cls instanceof KNearestNeighbours) {
            return createStatistics((KNearestNeighbours) cls, evaluation);
        } else if (cls instanceof StackingClassifier) {
            return createStatistics((StackingClassifier) cls, evaluation);
        } else if (cls instanceof J48) {
            return createStatistics((J48) cls, evaluation);
        } else {
            return null;
        }
    }

    private JTable createTable(ResultsModel model) {
        JDataTableBase jDataTableBase = new JDataTableBase(model);
        jDataTableBase.setAutoResizeOff(false);
        return jDataTableBase;
    }

}
