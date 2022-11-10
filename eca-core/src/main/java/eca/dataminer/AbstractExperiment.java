package eca.dataminer;

import eca.core.Assert;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.evaluation.EvaluationService;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Randomizable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static eca.util.Utils.shiftRight;

/**
 * Abstract class for automatic selection of optimal options
 * for classifiers based on experiment series.
 * <p>
 * Valid options are: <p>
 * <p>
 * Sets the number of folds (Default: 10) <p>
 * <p>
 * Sets the number of validations (Default: 10) <p>
 * <p>
 * Sets the number of iterations (Default: 100) <p>
 * <p>
 * Sets evaluation method (Default: TRAINING_DATA) <p>
 *
 * @param <T> classifier type
 * @author Roman Batygin
 */
public abstract class AbstractExperiment<T extends Classifier>
        implements Experiment<T>, IterableExperiment, Randomizable, Serializable {

    /**
     * Experiment history
     **/
    private final List<EvaluationResults> experiment;

    /**
     * Number of folds
     **/
    private int numFolds = 10;

    /**
     * Number of tests
     **/
    private int numTests = 1;

    /**
     * Number of iterations
     **/
    private int numIterations = 100;

    /**
     * Seed value for random generator
     */
    private int seed;

    /**
     * Evaluation method
     **/
    private EvaluationMethod evaluationMethod = EvaluationMethod.TRAINING_DATA;

    /**
     * Experiment history mode
     */
    private ExperimentHistoryMode experimentHistoryMode = ExperimentHistoryMode.FULL;

    /**
     * Best results number (used for {@link ExperimentHistoryMode#ONLY_BEST_MODELS} mode)
     */
    private int numBestResults = 5;

    /**
     * Training set
     **/
    private final Instances data;

    /**
     * Classifier object
     **/
    private final T classifier;

    private ExperimentHistoryModeVisitor experimentHistoryModeVisitor= new ExperimentHistoryModeVisitorImpl();

    private ClassifierComparator classifierComparator = new ClassifierComparator();

    protected Random random = new Random();

    protected AbstractExperiment(Instances data, T classifier) {
        this.data = data;
        this.classifier = classifier;
        this.experiment = new ArrayList<>();
    }

    @Override
    public void clearHistory() {
        getHistory().clear();
    }

    @Override
    public T getClassifier() {
        return classifier;
    }

    @Override
    public int getNumIterations() {
        return numIterations;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void setSeed(int seed) {
        this.seed = seed;
    }

    @Override
    public void setNumIterations(int numIterations) {
        Assert.greaterThanZero(numIterations, ExperimentDictionary.INVALID_NUM_EXPERIMENTS_ERROR_TEXT);
        this.numIterations = numIterations;
    }

    /**
     * Returns folds number.
     *
     * @return folds number
     */
    public int getNumFolds() {
        return numFolds;
    }

    /**
     * Return the evaluation method type.
     *
     * @return the evaluation method type
     */
    public EvaluationMethod getEvaluationMethod() {
        return evaluationMethod;
    }

    /**
     * Sets folds number.
     *
     * @param numFolds - folds number
     */
    public void setNumFolds(int numFolds) {
        this.numFolds = numFolds;
    }

    /**
     * Returns tests number.
     *
     * @return tests number
     */
    public int getNumTests() {
        return numTests;
    }

    /**
     * Sets tests number.
     *
     * @param numTests - tests number
     */
    public void setNumTests(int numTests) {
        this.numTests = numTests;
    }

    /**
     * Sets evaluation method type
     *
     * @param evaluationMethod - evaluation method type
     */
    public void setEvaluationMethod(EvaluationMethod evaluationMethod) {
        this.evaluationMethod = evaluationMethod;
    }

    /**
     * Sets experiment history mode
     *
     * @param experimentHistoryMode - experiment history mode
     */
    public void setExperimentHistoryMode(ExperimentHistoryMode experimentHistoryMode) {
        this.experimentHistoryMode = experimentHistoryMode;
    }

    /**
     * Return experiment history mode.
     *
     * @return experiment history mode
     */
    public ExperimentHistoryMode getExperimentHistoryMode() {
        return experimentHistoryMode;
    }

    /**
     * Sets best results number.
     *
     * @param numBestResults - best results number
     */
    public void setNumBestResults(int numBestResults) {
        this.numBestResults = numBestResults;
    }

    /**
     * Returns best results number.
     *
     * @return best results number
     */
    public int getNumBestResults() {
        return numBestResults;
    }

    @Override
    public Instances getData() {
        return data;
    }

    @Override
    public List<EvaluationResults> getHistory() {
        return experiment;
    }

    /**
     * Sorts experiment history by best results.
     */
    public synchronized void sortByBestResults() {
        experiment.sort(new ClassifierComparator());
    }

    @Override
    public void beginExperiment() throws Exception {
        IterativeExperiment iterativeExperiment = getIterativeExperiment();
        while (iterativeExperiment.hasNext()) {
            iterativeExperiment.next();
        }
    }

    protected final EvaluationResults evaluateModel(Classifier model) throws Exception {
        Evaluation evaluation = EvaluationService.evaluateModel(model, getData(),
                getEvaluationMethod(), getNumFolds(), getNumTests(), seed);
        EvaluationResults evaluationResults = new EvaluationResults(model, evaluation);
        addEvaluationResults(evaluationResults);
        return evaluationResults;
    }

    private void addEvaluationResults(EvaluationResults evaluationResults) {
        getExperimentHistoryMode().visit(experimentHistoryModeVisitor, evaluationResults);
    }

    /**
     * Experiment history mode visitor.
     */
    private class ExperimentHistoryModeVisitorImpl implements ExperimentHistoryModeVisitor, Serializable {

        @Override
        public void visitFull(EvaluationResults evaluationResults) {
            getHistory().add(evaluationResults);
        }

        @Override
        public void visitOnlyBestModels(EvaluationResults evaluationResults) {
            addIfBest(evaluationResults);
        }

        synchronized void addIfBest(EvaluationResults evaluationResults) {
            int pos = getPositionInHistory(evaluationResults);
            if (getHistory().size() == numBestResults) {
                if (pos < getHistory().size()) {
                    shiftRight(pos, getHistory());
                    getHistory().set(pos, evaluationResults);
                }
            } else {
                getHistory().add(pos, evaluationResults);
            }
        }

        int getPositionInHistory(EvaluationResults evaluationResults) {
            for (int i = 0; i < getHistory().size(); i++) {
                if (classifierComparator.compare(getHistory().get(i), evaluationResults) > 0) {
                    return i;
                }
            }
            return getHistory().size();
        }
    }

    /**
     * Base class for experiment iterative building.
     */
    protected abstract class AbstractIterativeBuilder implements IterativeExperiment {

        private int index;

        /**
         * Creates experiment iterative builder.
         */
        AbstractIterativeBuilder() {
            clearHistory();
        }

        /**
         * Increase experiment index.
         */
        void incrementIndex() {
            ++index;
        }

        @Override
        public boolean hasNext() {
            return index < getNumIterations();
        }

        @Override
        public int getPercent() {
            return index * 100 / getNumIterations();
        }

    }

}
