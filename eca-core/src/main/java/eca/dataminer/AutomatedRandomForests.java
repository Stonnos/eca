package eca.dataminer;

import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ConcurrentClassifier;
import eca.ensemble.EnsembleDictionary;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.ensemble.forests.RandomForestsType;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options for Random Forests algorithm.
 *
 * @author Roman Batygin
 */
public class AutomatedRandomForests extends AbstractExperiment<RandomForests> implements ConcurrentClassifier {

    private static final int MINIMUM_THREADS_NUMBER = 1;

    /**
     * Available decision tree types.
     */
    private static final DecisionTreeType[] DECISION_TREE_TYPE = DecisionTreeType.values();

    private static final RandomForestsType[] RANDOM_FORESTS_TYPE = RandomForestsType.values();

    /**
     * Available trees size for Random forests
     */
    private static final int[] AVAILABLE_TREE_NUM = {10, 25, 50, 75, 100};

    private static final int NUM_RANDOM_SPLITS_UPPER_BOUND = 25;

    private Integer numThreads = 1;

    /**
     * Creates automated Random forests object.
     *
     * @param data - training data object
     */
    public AutomatedRandomForests(Instances data) {
        super(data, new RandomForests());
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new RandomForestsIterativeBuilder();
    }

    @Override
    public Integer getNumThreads() {
        return numThreads;
    }

    @Override
    public void setNumThreads(Integer numThreads) {
        if (numThreads != null && numThreads < MINIMUM_THREADS_NUMBER) {
            throw new IllegalArgumentException(
                    String.format(EnsembleDictionary.INVALID_NUM_THREADS_ERROR_FORMAT, MINIMUM_THREADS_NUMBER));
        }
        this.numThreads = numThreads;
    }

    /**
     * Random forests iterative builder.
     */
    private class RandomForestsIterativeBuilder extends AbstractIterativeBuilder {

        @Override
        public EvaluationResults next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            incrementIndex();
            RandomForestsType randomForestsType = RANDOM_FORESTS_TYPE[random.nextInt(RANDOM_FORESTS_TYPE.length)];
            RandomForests randomForests = generateRandomForests(randomForestsType);
            generateCommonOptions(randomForests);
            return evaluateModel(randomForests);
        }

        RandomForests generateRandomForests(RandomForestsType randomForestsType) {
            switch (randomForestsType) {
                case RANDOM_FORESTS:
                    return new RandomForests();
                case EXTRA_TREES:
                    ExtraTreesClassifier extraTreesClassifier = new ExtraTreesClassifier();
                    extraTreesClassifier.setUseBootstrapSamples(random.nextBoolean());
                    int numRandomSplits = random.nextInt(NUM_RANDOM_SPLITS_UPPER_BOUND) + 1;
                    extraTreesClassifier.setNumRandomSplits(numRandomSplits);
                    return extraTreesClassifier;
                default:
                    throw new IllegalArgumentException(
                            String.format("Unexpected random forests type: %s", randomForestsType));
            }
        }

        void generateCommonOptions(RandomForests randomForests) {
            randomForests.setNumIterations(AVAILABLE_TREE_NUM[random.nextInt(AVAILABLE_TREE_NUM.length)]);
            DecisionTreeType decisionTreeType = DECISION_TREE_TYPE[random.nextInt(DECISION_TREE_TYPE.length)];
            randomForests.setDecisionTreeType(decisionTreeType);
            int numRandomAttrs = random.nextInt(getData().numAttributes() - 1) + 1;
            randomForests.setNumRandomAttr(numRandomAttrs);
            randomForests.setNumThreads(numThreads);
            randomForests.setSeed(getSeed());
        }
    }
}
