/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import javax.swing.JTable;
import java.text.DecimalFormat;
import javax.swing.table.AbstractTableModel;
import eca.trees.DecisionTreeClassifier;
import eca.neural.NeuralNetwork;
import eca.metrics.KNearestNeighbours;
import eca.ensemble.IterativeEnsembleClassifier;
import eca.ensemble.StackingClassifier;
import eca.regression.Logistic;
import weka.classifiers.Classifier;
import eca.core.evaluation.Evaluation;
import java.util.ArrayList;
import eca.beans.Entry;
import eca.gui.text.NumericFormat;

/**
 *
 * @author Рома
 */
public class StatisticsTableBuilder {

    private static final String[] title = {"Статистика", "Значение"};

    private final DecimalFormat FORMAT = NumericFormat.getInstance();

    /**
     *
     */
    private class ResultsModel extends AbstractTableModel {

        ArrayList<Entry> results = new ArrayList<>();

        public ResultsModel(Evaluation e, Classifier classifier) {
            results.add(new Entry("Исходные данные", e.getHeader().relationName()));
            results.add(new Entry("Число объектов", FORMAT.format(e.getData().numInstances())));
            results.add(new Entry("Число атрибутов", FORMAT.format(e.getData().numAttributes())));
            results.add(new Entry("Число классов", FORMAT.format(e.getData().numClasses())));
            results.add(new Entry("Классификатор", classifier.getClass().getSimpleName()));
            results.add(new Entry("Метод оценки точности",
                    !e.isKCrossValidationMethod() ? "Использование обучающей выборки"
                    : "Кросс - проверка, " + (e.getValidationsNum() > 1 ? e.getValidationsNum() + "*" : "")
                    + e.numFolds() + " - блочная"));
            results.add(new Entry("Число объектов тестовых данных", FORMAT.format(e.numInstances())));
            results.add(new Entry("Число правильно классифицированных объектов", FORMAT.format(e.correct())));
            results.add(new Entry("Число неправильно классифицированных объектов", FORMAT.format(e.incorrect())));
            results.add(new Entry("Точность классификатора, %", FORMAT.format(e.pctCorrect())));
            results.add(new Entry("Ошибка классификатора, %", FORMAT.format(e.pctIncorrect())));
            results.add(new Entry("Средняя абсолютная ошибка классификации", FORMAT.format(e.meanAbsoluteError())));
            results.add(new Entry("Среднеквадратическая ошибка классификации", FORMAT.format(e.rootMeanSquaredError())));
            if (e.isKCrossValidationMethod()) {
                results.add(new Entry("Дисперсия ошибки классификатора", FORMAT.format(e.varianceError())));
                double[] x = e.errorConfidenceInterval();
                results.add(new Entry("95% доверительный интервал ошибки классификатора", "[" + FORMAT.format(x[0]) +
                        "; " + FORMAT.format(x[1]) + "]"));
            }
        }

        @Override
        public int getColumnCount() {
            return title.length;
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

        public void addRow(Entry value) {
            results.add(value);
            fireTableRowsInserted(getRowCount(), getRowCount());
        }

        @Override
        public String getColumnName(int column) {
            return title[column];
        }
    }

    public StatisticsTableBuilder(int digits) {
        FORMAT.setMaximumFractionDigits(digits);
    }

    public final JTable createStatistica(DecisionTreeClassifier tree, Evaluation e) throws Exception {
        ResultsModel model = new ResultsModel(e, tree);
        model.addRow(new Entry("Число узлов", String.valueOf(tree.numNodes())));
        model.addRow(new Entry("Число листьев", String.valueOf(tree.numLeaves())));
        model.addRow(new Entry("Глубина дерева", String.valueOf(tree.depth())));
        return create(model);
    }

    public final JTable createStatistica(NeuralNetwork mlp, Evaluation e) throws Exception {
        ResultsModel model = new ResultsModel(e, mlp);
        model.addRow(new Entry("Число нейронов во входном слое", String.valueOf(mlp.network().inLayerNeuronsNum())));
        model.addRow(new Entry("Число нейронов в выходном слое", String.valueOf(mlp.network().outLayerNeuronsNum())));
        model.addRow(new Entry("Число скрытых слоев", String.valueOf(mlp.network().hiddenLayersNum())));
        model.addRow(new Entry("Структура скрытого слоя", mlp.network().getHiddenLayer()));
        model.addRow(new Entry("Число связей", String.valueOf(mlp.network().getLinksNum())));
        model.addRow(new Entry("Активационная функция нейронов скрытого слоя", mlp.network().getActivationFunction()
                .getClass().getSimpleName()));
        model.addRow(new Entry("Активационная функция нейронов выходного слоя", mlp.network()
                .getOutActivationFunction().getClass().getSimpleName()));
        model.addRow(new Entry("Алгоритм обучения", mlp.network().getLearningAlgorithm().getClass().getSimpleName()));
        return create(model);
    }

    public final JTable createStatistica(IterativeEnsembleClassifier cls, Evaluation e) throws Exception {
        ResultsModel model = new ResultsModel(e, cls);
        model.addRow(new Entry("Число классификаторов в ансамбле", String.valueOf(cls.numClassifiers())));
        return create(model);
    }

    public final JTable createStatistica(Logistic cls, Evaluation e) throws Exception {
        return create(new ResultsModel(e, cls));
    }

    public final JTable createStatistica(KNearestNeighbours cls, Evaluation e) throws Exception {
        ResultsModel model = new ResultsModel(e, cls);
        model.addRow(new Entry("Функция расстояния", cls.distance().getClass().getSimpleName()));
        return create(model);
    }

    public final JTable createStatistica(StackingClassifier cls, Evaluation e) throws Exception {
        ResultsModel model = new ResultsModel(e, cls);
        model.addRow(new Entry("Число классификаторов в ансамбле", String.valueOf(cls.numClassifiers())));
        model.addRow(new Entry("Мета-классификатор", cls.getMetaClassifier().getClass().getSimpleName()));
        return create(model);
    }

    public final JTable createStatistica(Classifier cls, Evaluation e) throws Exception {
        if (cls instanceof IterativeEnsembleClassifier) {
            return createStatistica((IterativeEnsembleClassifier) cls, e);
        } else if (cls instanceof NeuralNetwork) {
            return createStatistica((NeuralNetwork) cls, e);
        } else if (cls instanceof DecisionTreeClassifier) {
            return createStatistica((DecisionTreeClassifier) cls, e);
        } else if (cls instanceof Logistic) {
            return createStatistica((Logistic) cls, e);
        } else if (cls instanceof KNearestNeighbours) {
            return createStatistica((KNearestNeighbours) cls, e);
        } else if (cls instanceof StackingClassifier) {
            return createStatistica((StackingClassifier) cls, e);
        } else {
            return null;
        }
    }

    private JTable create(ResultsModel model) {
        JDataTableBase jDataTableBase = new JDataTableBase(model);
        jDataTableBase.setAutoResizeOff(false);
        return jDataTableBase;
    }

}
