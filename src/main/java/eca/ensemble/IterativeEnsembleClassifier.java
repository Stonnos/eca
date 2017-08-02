/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.InstancesHandler;
import eca.core.evaluation.Evaluation;
import eca.filter.MissingValuesFilter;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Class for generating iterative ensemble classification model. <p>
 *
 * Valid options are: <p>
 *
 * Set the number of iterations (Default: 10) <p>
 *
 * @author Рома
 */
public abstract class IterativeEnsembleClassifier extends AbstractClassifier
    implements Iterable, EnsembleClassifier, InstancesHandler {

    /** Initial training set **/
    private Instances initialData;

    /** Classifiers list **/
    protected ArrayList<Classifier> classifiers;

    /** Voting object **/
    protected VotingMethod votes;

    /** Filtered training set **/
    protected Instances filteredData;

    /** Number of iterations **/
    protected int numIterations = 10;

    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Sets the values of iterations number.
     * @param numIterations the values of iterations number
     * @exception IllegalArgumentException if the values of iterations number is less than 1
     */
    public final void setIterationsNum(int numIterations) {
        if (numIterations < 1)
            throw new IllegalArgumentException("Число итераций должно быть больше 1!");  
        this.numIterations = numIterations;
    }

    /**
     * Returns the values of iterations number.
     * @return the values of iterations number
     */
    public final int getIterationsNum() {
        return numIterations;
    }

    /**
     * Returns the values of classifiers number.
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
    public void buildClassifier(Instances data) throws Exception {
        IterativeBuilder i = getIterativeBuilder(data);
        while (i.hasNext()) {
            i.next();
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
    public ArrayList<Classifier> getStructure() throws Exception {
        ArrayList<Classifier> copies = new ArrayList<>(classifiers.size());
        for (Classifier c : classifiers)
            copies.add(AbstractClassifier.makeCopy(c));
        return copies;
    }

    protected abstract void initialize();
    
    protected final void checkModel() throws Exception {
        if (classifiers.isEmpty()) {
            throw new Exception("Не удалось построить модель: ни один классификатор не был включен в ансамбль!");
        }
    }
    
    protected final void checkForNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Negative value: " +
                    String.valueOf(value));
        }
    }
    
    /**
     * 
     */
    protected abstract class AbstractBuilder extends IterativeBuilder {
        
        protected AbstractBuilder(Instances dataSet) throws Exception {
            initialData = dataSet;
            filteredData = filter.filterInstances(initialData);
            classifiers = new ArrayList<>(numIterations);
            initialize();
        }
        
        @Override
        public int numIterations() {
            return numIterations;
        }
        
        @Override
        public Evaluation evaluation() throws Exception {
            if (!hasNext()) {
                Evaluation e = new Evaluation(initialData);
                e.evaluateModel(IterativeEnsembleClassifier.this, initialData);
                return e;
            }
            else return null;
        }
        
        @Override
        public boolean hasNext() {
            return index < numIterations;
        }
    }

    
}
