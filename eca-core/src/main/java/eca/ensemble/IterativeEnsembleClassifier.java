/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.InstancesHandler;
import eca.core.evaluation.Evaluation;
import eca.ensemble.voting.VotingMethod;
import eca.filter.MissingValuesFilter;
import eca.generators.NumberGenerator;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Randomizable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for generating iterative ensemble classification model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Set the number of threads (Default: 1) <p>
 * <p>
 *
 * @author Roman Batygin
 */
public abstract class IterativeEnsembleClassifier extends AbstractClassifier
        implements Iterable, EnsembleClassifier, InstancesHandler, ConcurrentClassifier, Randomizable {

    private static final int MINIMUM_ITERATIONS_NUMBER = 1;
    private static final int MINIMUM_THREADS_NUMBER = 1;

    /**
     * Initial training set
     **/
    private Instances initialData;

    /**
     * Number of iterations
     **/
    private int numIterations = 10;

    /**
     * Number of threads
     */
    private Integer numThreads = 1;

    /**
     * Seed value for random generator
     */
    private int seed;

    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Classifiers list
     **/
    protected ArrayList<Classifier> classifiers;

    /**
     * Voting object
     **/
    protected VotingMethod votes;

    protected Random random;

    protected int[] seeds;

    /**
     * Filtered training set
     **/
    protected Instances filteredData;

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
     * Sets the values of iterations number.
     *
     * @param numIterations the values of iterations number
     * @throws IllegalArgumentException if the values of iterations number is less than 1
     */
    public final void setIterationsNum(int numIterations) {
        if (numIterations < MINIMUM_ITERATIONS_NUMBER) {
            throw new IllegalArgumentException(
                    String.format(EnsembleDictionary.INVALID_NUM_ITS_ERROR_FORMAT, MINIMUM_ITERATIONS_NUMBER));
        }
        this.numIterations = numIterations;
    }

    /**
     * Returns the values of iterations number.
     *
     * @return the values of iterations number
     */
    public final int getIterationsNum() {
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

    /**
     * Returns the values of classifiers number.
     *
     * @return the values of classifiers number.
     */
    public final int numClassifiers() {
        return classifiers.size();
    }

    @Override
    public double[] distributionForInstance(Instance obj) throws Exception {
        return votes.distributionForInstance(filter.filterInstance(obj));
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new IterativeEnsembleBuilder(data);
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        if (getNumThreads() == null || getNumThreads() == 1) {
            IterativeBuilder i = getIterativeBuilder(data);
            while (i.hasNext()) {
                i.next();
            }
        } else {
            concurrentBuildClassifier(data);
        }
    }

    @Override
    public Instances getData() {
        return initialData;
    }

    @Override
    public double classifyInstance(Instance obj) throws Exception {
        return votes.classifyInstance(filter.filterInstance(obj));
    }

    @Override
    public List<Classifier> getStructure() throws Exception {
        ArrayList<Classifier> copies = new ArrayList<>(classifiers.size());
        for (Classifier classifier : classifiers) {
            copies.add(AbstractClassifier.makeCopy(classifier));
        }
        return copies;
    }

    /**
     * Initialized classifier options before building.
     *
     */
    protected abstract void initializeOptions();

    /**
     * Creates training data for next iteration.
     *
     * @return {@link Instances} object
     */
    protected abstract Instances createSample(int iteration) throws Exception;

    /**
     * Builds the next classifier model.
     *
     * @param iteration iteration number
     * @param data      {@link Instances} object
     * @return {@link Classifier} object
     */
    protected abstract Classifier buildNextClassifier(int iteration, Instances data) throws Exception;

    /**
     * Adds classifier to ensemble.
     *
     * @param classifier {@link Classifier} object
     * @param data       {@link Instances} object
     */
    protected abstract void addClassifier(Classifier classifier, Instances data) throws Exception;

    protected void checkModelForEmpty() throws Exception {
        if (classifiers.isEmpty()) {
            throw new Exception(EnsembleDictionary.EMPTY_ENSEMBLE_ERROR_TEXT);
        }
    }

    private void initializeData(Instances data) throws Exception {
        initialData = data;
        filteredData = filter.filterInstances(initialData);
        classifiers = new ArrayList<>(numIterations);
        random = new Random(seed);
    }

    private void initializeSeeds() {
        seeds = new int[getIterationsNum()];
        NumberGenerator.fillRandom(seeds, random);
    }

    /**
     * Implements concurrent algorithm for ensemble building.
     *
     * @param data {@link Instances} object
     * @throws Exception
     */
    private void concurrentBuildClassifier(Instances data) throws Exception {
        initializeData(data);
        initializeOptions();
        initializeSeeds();
        final CountDownLatch finishedLatch = new CountDownLatch(getIterationsNum());
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < getIterationsNum(); i++) {
            final int iteration = i;
            executorService.submit(() -> {
                try {
                    Instances sample = createSample(iteration);
                    Classifier classifier = buildNextClassifier(iteration, sample);
                    addClassifier(classifier, sample);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        checkModelForEmpty();
    }

    /**
     * Basic class for ensemble iterative building.
     */
    protected class IterativeEnsembleBuilder extends IterativeBuilder {

        Evaluation evaluation;

        IterativeEnsembleBuilder(Instances data) throws Exception {
            initializeData(data);
            initializeOptions();
            initializeSeeds();
        }

        @Override
        public int numIterations() {
            return numIterations;
        }

        @Override
        public Evaluation evaluation() throws Exception {
            if (evaluation == null) {
                evaluation = evaluateModel(IterativeEnsembleClassifier.this, initialData);
            }
            return evaluation;
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Instances sample = createSample(index);
            Classifier classifier = buildNextClassifier(index, sample);
            addClassifier(classifier, sample);
            if (index == getIterationsNum() - 1) {
                checkModelForEmpty();
            }
            return ++index;
        }

        @Override
        public boolean hasNext() {
            return index < numIterations;
        }
    }
}
