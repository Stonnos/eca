/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.PermutationsSearcher;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.StackingClassifier;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options
 * for stacking algorithm based on experiment series.
 *
 * @author Roman Batygin
 */
public class AutomatedStacking extends AbstractExperiment<StackingClassifier> {

    /**
     * Creates <tt>AutomatedStacking</tt> object with given options.
     *
     * @param classifier classifier object
     * @param data       training data
     */
    public AutomatedStacking(StackingClassifier classifier, Instances data) {
        super(data, classifier);
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new AutomatedStackingBuilder();
    }

    @Override
    public int getNumIterations() {
        int classifiersSize = getClassifier().getClassifiers().size();
        return classifiersSize * ExperimentUtil.getNumClassifiersCombinations(classifiersSize);
    }

    /**
     * Automated stacking iterative builder.
     */
    private class AutomatedStackingBuilder extends AbstractAutomatedExperiment {

        static final int META_MODEL_SELECTION_STATE = 0;
        static final int INIT_STATE = 1;
        static final int NEXT_PERMUTATION_STATE = 2;
        static final int MODEL_LEARNING_STATE = 3;

        /**
         * Current iteration
         */
        int index;
        /**
         * Machine current state
         */
        int state;
        /**
         * Individual classifiers number using by ensemble algorithms
         */
        int numIndividualClassifiers;
        /**
         * Meta classifier index
         */
        int metaClassifierIndex;
        /**
         * Initial individual classifiers set
         */
        ClassifiersSet classifiersSet = getClassifier().getClassifiers();
        ClassifiersSet currentSet = new ClassifiersSet();
        /**
         * Marks array for individual classifiers set using by next_permutation algorithm.
         * 1 means that classifier will be include in individual classifiers set, 0 - otherwise
         */
        int[] marks = new int[getClassifier().getClassifiers().size()];
        final PermutationsSearcher permutationsSearch = new PermutationsSearcher();
        final int numCombinations = getNumIterations();

        AutomatedStackingBuilder() {
            clearHistory();
        }

        @Override
        public EvaluationResults next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index++;
            return searchNext();
        }

        @Override
        public boolean hasNext() {
            return index < numCombinations;
        }

        @Override
        public int getPercent() {
            return index * 100 / numCombinations;
        }

        @Override
        public EvaluationResults nextState() throws Exception {
            switch (state) {
                case META_MODEL_SELECTION_STATE: {
                    processMetaModelSelectionState();
                    break;
                }
                case INIT_STATE: {
                    processInitState();
                    break;
                }
                case NEXT_PERMUTATION_STATE: {
                    processNextPermutationState();
                    break;
                }
                case MODEL_LEARNING_STATE: {
                    return processModelLearningState();
                }
            }
            return null;
        }

        void processMetaModelSelectionState() throws Exception {
            Classifier metaClassifier = getClassifier().getClassifiers().getClassifierCopy(metaClassifierIndex);
            getClassifier().setMetaClassifier(metaClassifier);
            state = INIT_STATE;
        }

        void processInitState() {
            if (numIndividualClassifiers == marks.length) {
                metaClassifierIndex++;
                numIndividualClassifiers = 0;
                state = META_MODEL_SELECTION_STATE;
            } else {
                fillMarks();
                permutationsSearch.setValues(marks);
                state = NEXT_PERMUTATION_STATE;
            }
        }

        void processNextPermutationState() {
            if (permutationsSearch.nextPermutation()) {
                initializeClassifiersSet();
                state = MODEL_LEARNING_STATE;
            } else {
                numIndividualClassifiers++;
                state = INIT_STATE;
            }
        }

        EvaluationResults processModelLearningState() throws Exception {
            state = NEXT_PERMUTATION_STATE;
            StackingClassifier nextModel
                    = (StackingClassifier) AbstractClassifier.makeCopy(getClassifier());
            nextModel.setSeed(getSeed());
            nextModel.setClassifiers(new ClassifiersSet(currentSet));
            return evaluateModel(nextModel);
        }

        void fillMarks() {
            for (int j = 0; j < marks.length; j++) {
                marks[j] = j <= numIndividualClassifiers ? 1 : 0;
            }
        }

        void initializeClassifiersSet() {
            currentSet.clear();
            for (int k = 0; k < marks.length; k++) {
                if (marks[k] == 1) {
                    currentSet.addClassifier(classifiersSet.getClassifier(k));
                }
            }
        }
    }

}
