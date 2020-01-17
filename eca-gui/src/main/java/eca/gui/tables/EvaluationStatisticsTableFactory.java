/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.core.evaluation.Evaluation;
import eca.ensemble.IterativeEnsembleClassifier;
import eca.ensemble.StackingClassifier;
import eca.gui.tables.models.EvaluationStatisticsModel;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.DecisionTreeClassifier;
import eca.trees.J48;
import eca.util.Entry;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import weka.classifiers.Classifier;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.ACTIVATION_FUNCTION_HIDDEN_LAYER_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.ACTIVATION_FUNCTION_OUT_LAYER_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.CLASSIFIERS_IN_ENSEMBLE_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.DISTANCE_FUNCTION_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.HIDDEN_LAYERS_NUM_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.HIDDEN_LAYER_STRUCTURE_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.IN_LAYER_NEURONS_NUM_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.LEARNING_ALGORITHM_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.LINKS_NUM_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.META_CLASSIFIER_NAME_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.NUMBER_OF_LEAVES_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.NUMBER_OF_NODES_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.OUT_LAYER_NEURONS_NUM_TEXT;
import static eca.gui.dictionary.EvaluationStatisticsDictionary.TREE_DEPTH_TEXT;

/**
 * Implements building classifiers evaluation results represented in table.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EvaluationStatisticsTableFactory {

    private static List<ClassifierConditionRule> classifierConditionRules;

    static {
        classifierConditionRules = newArrayList();
        classifierConditionRules.add(new DecisionTreeConditionRule());
        classifierConditionRules.add(new NeuralNetworkConditionRule());
        classifierConditionRules.add(new IterativeEnsembleClassifierConditionRule());
        classifierConditionRules.add(new LogisticConditionRule());
        classifierConditionRules.add(new KNearestNeighboursConditionRule());
        classifierConditionRules.add(new StackingClassifierConditionRule());
        classifierConditionRules.add(new J48ConditionRule());
    }

    /**
     * Builds evaluation statistics model.
     *
     * @param classifier        - classifier object
     * @param evaluation        - classifier evaluation
     * @param maxFractionDigits - maximum fraction digits
     * @return evaluation statistics model
     */
    public static EvaluationStatisticsModel buildEvaluationStatisticsTable(Classifier classifier,
                                                                           Evaluation evaluation,
                                                                           int maxFractionDigits) {
        ClassifierConditionRule classifierConditionRule = classifierConditionRules.stream()
                .filter(rule -> rule.matches(classifier))
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle %s classifier", classifier.getClass().getSimpleName())));
        return classifierConditionRule.createEvaluationStatisticsModel(classifier, evaluation, maxFractionDigits);
    }

    @RequiredArgsConstructor
    private static class ClassifierConditionRule<T extends Classifier> {

        private final Class<T> clazz;

        public boolean matches(T classifier) {
            return clazz.isAssignableFrom(classifier.getClass());
        }

        public EvaluationStatisticsModel createEvaluationStatisticsModel(T classifier, Evaluation evaluation,
                                                                         int maxFractionDigits) {
            return new EvaluationStatisticsModel(evaluation, classifier, maxFractionDigits);
        }
    }

    private static class DecisionTreeConditionRule extends ClassifierConditionRule<DecisionTreeClassifier> {

        DecisionTreeConditionRule() {
            super(DecisionTreeClassifier.class);
        }

        @Override
        public EvaluationStatisticsModel createEvaluationStatisticsModel(DecisionTreeClassifier classifier,
                                                                         Evaluation evaluation,
                                                                         int maxFractionDigits) {
            EvaluationStatisticsModel evaluationStatisticsModel =
                    super.createEvaluationStatisticsModel(classifier, evaluation, maxFractionDigits);
            evaluationStatisticsModel.addRow(new Entry<>(NUMBER_OF_NODES_TEXT, String.valueOf(classifier.numNodes())));
            evaluationStatisticsModel.addRow(
                    new Entry<>(NUMBER_OF_LEAVES_TEXT, String.valueOf(classifier.numLeaves())));
            evaluationStatisticsModel.addRow(new Entry<>(TREE_DEPTH_TEXT, String.valueOf(classifier.depth())));
            return evaluationStatisticsModel;
        }
    }

    private static class NeuralNetworkConditionRule extends ClassifierConditionRule<NeuralNetwork> {

        NeuralNetworkConditionRule() {
            super(NeuralNetwork.class);
        }

        @Override
        public EvaluationStatisticsModel createEvaluationStatisticsModel(NeuralNetwork classifier,
                                                                         Evaluation evaluation,
                                                                         int maxFractionDigits) {
            EvaluationStatisticsModel evaluationStatisticsModel =
                    super.createEvaluationStatisticsModel(classifier, evaluation, maxFractionDigits);
            evaluationStatisticsModel.addRow(new Entry<>(IN_LAYER_NEURONS_NUM_TEXT,
                    String.valueOf(classifier.getMultilayerPerceptron().getNumInNeurons())));
            evaluationStatisticsModel.addRow(new Entry<>(OUT_LAYER_NEURONS_NUM_TEXT,
                    String.valueOf(classifier.getMultilayerPerceptron().getNumOutNeurons())));
            evaluationStatisticsModel.addRow(new Entry<>(HIDDEN_LAYERS_NUM_TEXT,
                    String.valueOf(classifier.getMultilayerPerceptron().hiddenLayersNum())));
            evaluationStatisticsModel.addRow(
                    new Entry<>(HIDDEN_LAYER_STRUCTURE_TEXT, classifier.getMultilayerPerceptron().getHiddenLayer()));
            evaluationStatisticsModel.addRow(
                    new Entry<>(LINKS_NUM_TEXT, String.valueOf(classifier.getMultilayerPerceptron().getLinksNum())));
            evaluationStatisticsModel.addRow(new Entry<>(ACTIVATION_FUNCTION_HIDDEN_LAYER_TEXT,
                    classifier.getMultilayerPerceptron().getActivationFunction().getActivationFunctionType().getDescription()));
            evaluationStatisticsModel.addRow(new Entry<>(ACTIVATION_FUNCTION_OUT_LAYER_TEXT,
                    classifier.getMultilayerPerceptron().getOutActivationFunction().getActivationFunctionType().getDescription()));
            evaluationStatisticsModel.addRow(new Entry<>(LEARNING_ALGORITHM_TEXT,
                    classifier.getMultilayerPerceptron().getLearningAlgorithm().getClass().getSimpleName()));
            return evaluationStatisticsModel;
        }
    }

    private static class IterativeEnsembleClassifierConditionRule
            extends ClassifierConditionRule<IterativeEnsembleClassifier> {

        IterativeEnsembleClassifierConditionRule() {
            super(IterativeEnsembleClassifier.class);
        }

        @Override
        public EvaluationStatisticsModel createEvaluationStatisticsModel(IterativeEnsembleClassifier classifier,
                                                                         Evaluation evaluation,
                                                                         int maxFractionDigits) {
            EvaluationStatisticsModel evaluationStatisticsModel =
                    super.createEvaluationStatisticsModel(classifier, evaluation, maxFractionDigits);
            evaluationStatisticsModel.addRow(
                    new Entry<>(CLASSIFIERS_IN_ENSEMBLE_TEXT, String.valueOf(classifier.numClassifiers())));
            return evaluationStatisticsModel;
        }
    }

    private static class LogisticConditionRule extends ClassifierConditionRule<Logistic> {

        LogisticConditionRule() {
            super(Logistic.class);
        }
    }

    private static class KNearestNeighboursConditionRule extends ClassifierConditionRule<KNearestNeighbours> {

        KNearestNeighboursConditionRule() {
            super(KNearestNeighbours.class);
        }

        @Override
        public EvaluationStatisticsModel createEvaluationStatisticsModel(KNearestNeighbours kNearestNeighbours,
                                                                         Evaluation evaluation,
                                                                         int maxFractionDigits) {
            EvaluationStatisticsModel evaluationStatisticsModel =
                    super.createEvaluationStatisticsModel(kNearestNeighbours, evaluation, maxFractionDigits);
            evaluationStatisticsModel.addRow(new Entry<>(DISTANCE_FUNCTION_TEXT,
                    kNearestNeighbours.getDistance().getDistanceType().getDescription()));
            return evaluationStatisticsModel;
        }
    }

    private static class StackingClassifierConditionRule extends ClassifierConditionRule<StackingClassifier> {

        StackingClassifierConditionRule() {
            super(StackingClassifier.class);
        }

        @Override
        public EvaluationStatisticsModel createEvaluationStatisticsModel(StackingClassifier stackingClassifier,
                                                                         Evaluation evaluation,
                                                                         int maxFractionDigits) {
            EvaluationStatisticsModel evaluationStatisticsModel =
                    super.createEvaluationStatisticsModel(stackingClassifier, evaluation, maxFractionDigits);
            evaluationStatisticsModel.addRow(
                    new Entry<>(CLASSIFIERS_IN_ENSEMBLE_TEXT, String.valueOf(stackingClassifier.numClassifiers())));
            evaluationStatisticsModel.addRow(
                    new Entry<>(META_CLASSIFIER_NAME_TEXT,
                            stackingClassifier.getMetaClassifier().getClass().getSimpleName()));
            return evaluationStatisticsModel;
        }
    }

    private static class J48ConditionRule extends ClassifierConditionRule<J48> {

        J48ConditionRule() {
            super(J48.class);
        }

        @Override
        public EvaluationStatisticsModel createEvaluationStatisticsModel(J48 classifier, Evaluation evaluation,
                                                                         int maxFractionDigits) {
            EvaluationStatisticsModel evaluationStatisticsModel =
                    super.createEvaluationStatisticsModel(classifier, evaluation, maxFractionDigits);
            evaluationStatisticsModel.addRow(
                    new Entry<>(NUMBER_OF_NODES_TEXT, String.valueOf((int) classifier.measureTreeSize())));
            evaluationStatisticsModel.addRow(
                    new Entry<>(NUMBER_OF_LEAVES_TEXT, String.valueOf((int) classifier.measureNumLeaves())));
            return evaluationStatisticsModel;
        }
    }
}
