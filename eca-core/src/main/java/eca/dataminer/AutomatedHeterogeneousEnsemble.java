/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.PermutationsSearcher;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.sampling.SamplingMethod;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options
 * for heterogeneous ensemble algorithms based on experiment series.
 *
 * @author Roman Batygin
 */
public class AutomatedHeterogeneousEnsemble extends AbstractExperiment<AbstractHeterogeneousClassifier> {

    /**
     * Available sampling methods
     **/
    private static final SamplingMethod[] SAMPLE_METHOD = SamplingMethod.values();

    /**
     * Available voting methods
     **/
    private static final boolean[] VOTING_METHOD = {true, false};

    /**
     * Available classifier selection methods
     **/
    private static final boolean[] CLASSIFIER_SELECTION_METHOD = {true, false};

    /**
     * Creates <tt>AutomatedHeterogeneousEnsemble</tt> object with given options.
     *
     * @param classifier heterogeneous classifier algorithm
     * @param data       training set
     */
    public AutomatedHeterogeneousEnsemble(AbstractHeterogeneousClassifier classifier, Instances data) {
        super(data, classifier);
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new AutomatedHeterogeneousBuilder();
    }

    /**
     * Automated heterogeneous classifier iterative builder.
     */
    private class AutomatedHeterogeneousBuilder extends AbstractAutomatedExperiment {

        static final int INIT_STATE = 0;
        static final int NEXT_PERMUTATION_STATE = 1;
        static final int MODEL_LEARNING_STATE = 2;

        int i, s, a = -1, index, state, it;
        ClassifiersSet set = getClassifier().getClassifiersSet();
        ClassifiersSet currentSet = new ClassifiersSet();
        int[] marks = new int[getClassifier().getClassifiersSet().size()];
        PermutationsSearcher permutationsSearch = new PermutationsSearcher();
        final int numCombinations = getNumCombinations();

        AutomatedHeterogeneousBuilder() {
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

                case INIT_STATE: {
                    for (int j = 0; j < marks.length; j++) {
                        marks[j] = j <= it ? 1 : 0;
                    }
                    permutationsSearch.setValues(marks);
                    state = NEXT_PERMUTATION_STATE;
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
                    if (getClassifier() instanceof HeterogeneousClassifier) {
                        for (; s < SAMPLE_METHOD.length; s++) {
                            for (; i < CLASSIFIER_SELECTION_METHOD.length; i++) {
                                for (++a; a < VOTING_METHOD.length; ) {
                                    HeterogeneousClassifier nextModel =
                                            (HeterogeneousClassifier) AbstractClassifier.makeCopy(getClassifier());
                                    nextModel.setSeed(getSeed());
                                    nextModel.setSamplingMethod(SAMPLE_METHOD[s]);
                                    nextModel.setUseRandomClassifier(CLASSIFIER_SELECTION_METHOD[i]);
                                    nextModel.setUseWeightedVotesMethod(VOTING_METHOD[a]);
                                    nextModel.setClassifiersSet(new ClassifiersSet(currentSet));
                                    return evaluateModel(nextModel);
                                }
                                a = -1;
                            }
                            i = 0;
                        }
                        s = 0;
                        state = NEXT_PERMUTATION_STATE;
                    } else {
                        state = NEXT_PERMUTATION_STATE;
                        AbstractHeterogeneousClassifier nextModel
                                = (AbstractHeterogeneousClassifier) AbstractClassifier.makeCopy(getClassifier());
                        nextModel.setSeed(getSeed());
                        nextModel.setClassifiersSet(new ClassifiersSet(currentSet));
                        return evaluateModel(nextModel);
                    }
                    break;
                }

            }

            return null;
        }

        int getNumCombinations() {
            return getAdditionalParamCombinationsNum() * ExperimentUtil.getNumClassifiersCombinations(set.size());
        }

        int getAdditionalParamCombinationsNum() {
            if (getClassifier() instanceof HeterogeneousClassifier) {
                return SAMPLE_METHOD.length * CLASSIFIER_SELECTION_METHOD.length * VOTING_METHOD.length;
            } else {
                return 1;
            }
        }

    }

}
