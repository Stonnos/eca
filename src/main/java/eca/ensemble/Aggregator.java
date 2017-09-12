/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.ensemble.IterativeEnsembleClassifier;
import weka.core.Instance;
import weka.core.Utils;

import java.util.ArrayList;

/**
 * Implements ensemble classification results aggregating.
 *
 * @author Roman Batygin
 */
public class Aggregator implements java.io.Serializable {

    /**
     * Iterative ensemble model
     **/
    private IterativeEnsembleClassifier classifier;

    /**
     * Creates <tt>Aggregator</tt> object.
     *
     * @param classifier <tt>IterativeEnsembleClassifier</tt> object
     */
    public Aggregator(IterativeEnsembleClassifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Returns <tt>IterativeEnsembleClassifier</tt> object.
     *
     * @return <tt>IterativeEnsembleClassifier</tt> object
     */
    public IterativeEnsembleClassifier classifier() {
        return classifier;
    }

    /**
     * Classify instance by classifier at the specified position.
     *
     * @param i   index of the classifier
     * @param obj instance object
     * @return class value
     * @throws Exception
     */
    public double classifyInstance(int i, Instance obj) throws Exception {
        return classifier.classifiers.get(i).classifyInstance(obj);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param i   index of the classifier
     * @param obj instance object
     * @return the array of classes probabilities
     * @throws Exception
     */
    public double[] distributionForInstance(int i, Instance obj) throws Exception {
        return classifier.classifiers.get(i).distributionForInstance(obj);
    }

    /**
     * Aggregate classification results of individual models
     * using majority votes method.
     *
     * @param obj instance object
     * @return class value
     * @throws Exception
     */
    public double aggregate(Instance obj) throws Exception {
        return aggregate(obj, null);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param obj instance object
     * @return the array of classes probabilities
     * @throws Exception
     */
    public double[] distributionForInstance(Instance obj) throws Exception {
        return distributionForInstance(obj, null);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param obj     instance object
     * @param weights classifiers weight
     * @return the array of classes probabilities
     * @throws Exception
     */
    public double[] distributionForInstance(Instance obj, ArrayList<Double> weights) throws Exception {
        double[] sums;
        if (classifier.classifiers.size() == 1) {
            return classifier.classifiers.get(0).distributionForInstance(obj);
        } else if (weights == null) {
            sums = new double[classifier.filteredData.numClasses()];
            for (int i = 0; i < classifier.classifiers.size(); i++) {
                double[] distr = distributionForInstance(i, obj);
                for (int j = 0; j < distr.length; j++) {
                    sums[j] += distr[j];
                }
            }
        } else {
            sums = getVoices(obj, weights);
        }
        if (Utils.eq(Utils.sum(sums), 0)) {
            return sums;
        } else {
            Utils.normalize(sums);
        }
        return sums;
    }

    /**
     * Returns the voices array for given instance.
     *
     * @param obj     instance object
     * @param weights classifiers weight
     * @return the voices array for given instance
     * @throws Exception
     */
    public double[] getVoices(Instance obj, ArrayList<Double> weights) throws Exception {
        double[] voices = new double[obj.numClasses()];
        for (int i = 0; i < classifier.classifiers.size(); i++) {
            int classIndex = (int) classifyInstance(i, obj);
            voices[classIndex] += weights == null ? 1.0 : weights.get(i);
        }
        return voices;
    }

    /**
     * Aggregate classification results of individual models
     * using weighted votes method.
     *
     * @param obj     instance object
     * @param weights instance object
     * @return class value
     * @throws Exception
     */
    public double aggregate(Instance obj, ArrayList<Double> weights) throws Exception {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        double[] voices = getVoices(obj, weights);
        double classValue = 0.0, maxVoices = 0.0;
        for (int i = 0; i < voices.length; i++) {
            if (voices[i] > maxVoices) {
                maxVoices = voices[i];
                classValue = i;
            }
        }
        return classValue;
    }

}
