package eca.dataminer;

import com.google.common.collect.ImmutableList;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.DecisionTreeTypeVisitor;
import eca.generators.NumberGenerator;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import eca.trees.ID3;
import weka.core.Instances;
import weka.core.Utils;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options for decision tree algorithms.
 *
 * @author Roman Batygin
 */
public class AutomatedDecisionTree extends AbstractExperiment<DecisionTreeClassifier> {

    private static final int MIN_NUM_OBJ = 2;
    private static final int MAX_NUM_OBJ = 10;

    private static final int MIN_HEIGHT = 0;

    /**
     * Available alpha values for hi square test used by CHAID algorithm
     */
    private static final List<Double> ALPHAS =
            ImmutableList.of(0.995d, 0.99d, 0.975d, 0.95d, 0.75d, 0.5d, 0.25d, 0.1d, 0.05d, 0.025d, 0.01d, 0.005d);

    /**
     * Available decision tree types.
     */
    private static final DecisionTreeType[] DECISION_TREE_TYPES = DecisionTreeType.values();

    /**
     * Creates automated Random forests object.
     *
     * @param data - training data object
     */
    public AutomatedDecisionTree(Instances data) {
        super(data, new CART());
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new DecisionTreeIterativeBuilder();
    }

    /**
     * Decision tree iterative builder.
     */
    private class DecisionTreeIterativeBuilder extends AbstractIterativeBuilder {

        int maxHeight;

        DecisionTreeIterativeBuilder() {
            this.maxHeight = (int) Utils.log2(getData().numInstances());
        }

        @Override
        public EvaluationResults next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            incrementIndex();
            DecisionTreeType decisionTreeType = DECISION_TREE_TYPES[random.nextInt(DECISION_TREE_TYPES.length)];
            DecisionTreeClassifier decisionTreeClassifier = decisionTreeType.handle(
                    new DecisionTreeTypeVisitor<DecisionTreeClassifier>() {
                        @Override
                        public DecisionTreeClassifier handleCartTree() {
                            return new CART();
                        }

                        @Override
                        public DecisionTreeClassifier handleId3Tree() {
                            return new ID3();
                        }

                        @Override
                        public DecisionTreeClassifier handleC45Tree() {
                            return new C45();
                        }

                        @Override
                        public DecisionTreeClassifier handleChaidTree() {
                            CHAID chaid = new CHAID();
                            chaid.setAlpha(ALPHAS.get(random.nextInt(ALPHAS.size())));
                            return chaid;
                        }
                    });
            generateCommonOptions(decisionTreeClassifier);
            return evaluateModel(decisionTreeClassifier);
        }

        void generateCommonOptions(DecisionTreeClassifier decisionTreeClassifier) {
            decisionTreeClassifier.setMinObj(NumberGenerator.randomInt(random, MIN_NUM_OBJ, MAX_NUM_OBJ));
            decisionTreeClassifier.setMaxDepth(NumberGenerator.randomInt(random, MIN_HEIGHT, maxHeight));
            decisionTreeClassifier.setUseBinarySplits(random.nextBoolean());
            decisionTreeClassifier.setSeed(getSeed());
        }
    }
}
