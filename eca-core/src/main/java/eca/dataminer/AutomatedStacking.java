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

    /**
     * Automated stacking iterative builder.
     */
    private class AutomatedStackingBuilder extends AbstractAutomatedExperiment {

        static final int META_MODEL_SELECTION_STATE = 0;
        static final int INIT_STATE = 1;
        static final int NEXT_PERMUTATION_STATE = 2;
        static final int MODEL_LEARNING_STATE = 3;

        int index;
        int state;
        int it;
        int metaClsIndex;
        ClassifiersSet set = getClassifier().getClassifiers();
        ClassifiersSet currentSet = new ClassifiersSet();
        int[] marks = new int[getClassifier().getClassifiers().size()];
        PermutationsSearcher permutationsSearch = new PermutationsSearcher();
        int numCombinations = getNumCombinations();

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
                    Classifier metaClassifier = getClassifier().getClassifiers().getClassifierCopy(metaClsIndex);
                    getClassifier().setMetaClassifier(metaClassifier);
                    state = INIT_STATE;
                    break;
                }
                case INIT_STATE: {
                    if (it == marks.length) {
                        metaClsIndex++;
                        it = 0;
                        state = META_MODEL_SELECTION_STATE;
                    } else {
                        for (int j = 0; j < marks.length; j++) {
                            marks[j] = j <= it ? 1 : 0;
                        }
                        permutationsSearch.setValues(marks);
                        state = NEXT_PERMUTATION_STATE;
                    }
                    break;
                }
                case NEXT_PERMUTATION_STATE: {
                    if (permutationsSearch.nextPermutation()) {
                        currentSet.clear();
                        for (int k = 0; k < marks.length; k++) {
                            if (marks[k] == 1) {
                                currentSet.addClassifier(set.getClassifier(k));
                            }
                        }
                        state = MODEL_LEARNING_STATE;
                    } else {
                        it++;
                        state = INIT_STATE;
                    }
                    break;
                }
                case MODEL_LEARNING_STATE: {
                    state = NEXT_PERMUTATION_STATE;
                    StackingClassifier nextModel
                            = (StackingClassifier) AbstractClassifier.makeCopy(getClassifier());
                    nextModel.setClassifiers(currentSet.clone());
                    return evaluateModel(nextModel);
                }
            }
            return null;
        }

        int getNumCombinations() {
            return set.size() * ExperimentUtil.getNumClassifiersCombinations(set.size());
        }

    }

}
