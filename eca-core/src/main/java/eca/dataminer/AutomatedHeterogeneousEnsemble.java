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

    @Override
    public int getNumIterations() {
        return getAdditionalParamCombinationsNum() *
                ExperimentUtil.getNumClassifiersCombinations(getClassifier().getClassifiersSet().size());
    }

    private int getAdditionalParamCombinationsNum() {
        if (getClassifier() instanceof HeterogeneousClassifier) {
            return SAMPLE_METHOD.length * CLASSIFIER_SELECTION_METHOD.length * VOTING_METHOD.length;
        } else {
            return 1;
        }
    }

    /**
     * Automated heterogeneous classifier iterative builder.
     */
    private class AutomatedHeterogeneousBuilder extends AbstractAutomatedExperiment {

        static final int INIT_STATE = 0;
        static final int NEXT_PERMUTATION_STATE = 1;
        static final int MODEL_LEARNING_STATE = 2;

        /**
         * Classifier selection method index
         */
        int classifierSelectionMethodIndex;
        /**
         * Sample selection method index
         */
        int sampleMethodIndex;
        /**
         * Voting selection method index
         */
        int votingMethodIndex = -1;
        /**
         * Individual classifiers number using by ensemble algorithms
         */
        int numIndividualClassifiers;
        /**
         * Machine current state
         */
        int state;
        /**
         * Current iteration
         */
        int index;
        /**
         * Initial individual classifiers set
         */
        ClassifiersSet classifiersSet = getClassifier().getClassifiersSet();
        /**
         * Current individual classifiers set
         */
        ClassifiersSet currentSet = new ClassifiersSet();
        /**
         * Marks array for individual classifiers set using by next_permutation algorithm.
         * 1 means that classifier will be include in individual classifiers set, 0 - otherwise
         */
        int[] marks = new int[getClassifier().getClassifiersSet().size()];
        final PermutationsSearcher permutationsSearch = new PermutationsSearcher();
        final int numCombinations = getNumIterations();

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
                default:
                    throw new IllegalStateException(String.format("Unknown state [%d]", state));
            }
            return null;
        }

        void processInitState() {
            fillMarks();
            permutationsSearch.setValues(marks);
            state = NEXT_PERMUTATION_STATE;
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
            if (getClassifier() instanceof HeterogeneousClassifier) {
                for (; sampleMethodIndex < SAMPLE_METHOD.length; sampleMethodIndex++) {
                    for (; classifierSelectionMethodIndex < CLASSIFIER_SELECTION_METHOD.length;
                         classifierSelectionMethodIndex++) {
                        votingMethodIndex++;
                        if (votingMethodIndex < VOTING_METHOD.length) {
                            HeterogeneousClassifier nextModel =
                                    (HeterogeneousClassifier) AbstractClassifier.makeCopy(getClassifier());
                            nextModel.setSeed(getSeed());
                            nextModel.setSamplingMethod(SAMPLE_METHOD[sampleMethodIndex]);
                            nextModel.setUseRandomClassifier(
                                    CLASSIFIER_SELECTION_METHOD[classifierSelectionMethodIndex]);
                            nextModel.setUseWeightedVotes(VOTING_METHOD[votingMethodIndex]);
                            nextModel.setClassifiersSet(new ClassifiersSet(currentSet));
                            return evaluateModel(nextModel);
                        }
                        votingMethodIndex = -1;
                    }
                    classifierSelectionMethodIndex = 0;
                }
                sampleMethodIndex = 0;
                state = NEXT_PERMUTATION_STATE;
            } else {
                state = NEXT_PERMUTATION_STATE;
                AbstractHeterogeneousClassifier nextModel
                        = (AbstractHeterogeneousClassifier) AbstractClassifier.makeCopy(getClassifier());
                nextModel.setSeed(getSeed());
                nextModel.setClassifiersSet(new ClassifiersSet(currentSet));
                return evaluateModel(nextModel);
            }
            return null;
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
