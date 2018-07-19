package eca.dataminer;

import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ConcurrentClassifier;
import eca.ensemble.EnsembleDictionary;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.ensemble.forests.RandomForestsType;
import eca.ensemble.forests.RandomForestsTypeVisitor;
import eca.generators.NumberGenerator;
import weka.core.Instances;
import weka.core.Utils;

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

    private static final int MIN_NUM_OBJ = 2;
    private static final int MAX_NUM_OBJ = 10;

    private static final int MIN_TREES_NUM = 10;
    private static final int MAX_TREES_NUM = 50;

    private static final int NUM_RANDOM_SPLITS_LOWER_BOUND = 1;
    private static final int NUM_RANDOM_SPLITS_UPPER_BOUND = 50;

    private static final int MIN_HEIGHT = 0;

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

        int maxHeight;

        RandomForestsIterativeBuilder() {
            this.maxHeight = (int) Utils.log2(getData().numInstances());
        }

        @Override
        public EvaluationResults next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            incrementIndex();
            RandomForestsType randomForestsType = RANDOM_FORESTS_TYPE[random.nextInt(RANDOM_FORESTS_TYPE.length)];
            RandomForests randomForests = randomForestsType.handle(new RandomForestsTypeVisitor<RandomForests>() {
                @Override
                public RandomForests caseRandomForests() {
                    return new RandomForests();
                }

                @Override
                public RandomForests caseExtraTrees() {
                    ExtraTreesClassifier extraTreesClassifier = new ExtraTreesClassifier();
                    extraTreesClassifier.setUseBootstrapSamples(random.nextBoolean());
                    extraTreesClassifier.setNumRandomSplits(
                            NumberGenerator.randomInt(random, NUM_RANDOM_SPLITS_LOWER_BOUND,
                                    NUM_RANDOM_SPLITS_UPPER_BOUND));
                    return extraTreesClassifier;
                }
            });
            generateCommonOptions(randomForests);
            return evaluateModel(randomForests);
        }

        void generateCommonOptions(RandomForests randomForests) {
            randomForests.setNumIterations(NumberGenerator.randomInt(random, MIN_TREES_NUM, MAX_TREES_NUM));
            randomForests.setMinObj(NumberGenerator.randomInt(random, MIN_NUM_OBJ, MAX_NUM_OBJ));
            randomForests.setMaxDepth(NumberGenerator.randomInt(random, MIN_HEIGHT, maxHeight));
            DecisionTreeType decisionTreeType = DECISION_TREE_TYPE[random.nextInt(DECISION_TREE_TYPE.length)];
            randomForests.setDecisionTreeType(decisionTreeType);
            int numRandomAttrs = random.nextInt(getData().numAttributes() - 1) + 1;
            randomForests.setNumRandomAttr(numRandomAttrs);
            randomForests.setNumThreads(numThreads);
            randomForests.setSeed(getSeed());
        }
    }
}
