/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.util.Utils;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;
import java.util.Objects;

/**
 * Implements ensemble classification results aggregating.
 *
 * @author Roman Batygin
 */
public class Aggregator implements java.io.Serializable {

    /**
     * Iterative ensemble model
     **/
    private List<Classifier> classifiers;

    /**
     * Instances model
     */
    private Instances instances;

    /**
     * Creates aggregator object.
     *
     * @param classifiers - classifiers list
     */
    public Aggregator(List<Classifier> classifiers, Instances instances) {
        this.classifiers = classifiers;
        this.instances = instances;
    }

    /**
     * Gets classifiers list.
     *
     * @return classifiers list
     */
    public List<Classifier> getClassifiers() {
        return classifiers;
    }

    /**
     * Gets instances (training data).
     *
     * @return instances object
     */
    public Instances getInstances() {
        return instances;
    }

    /**
     * Classify instance by classifier at the specified position.
     *
     * @param i   index of the classifier
     * @param obj instance object
     * @return class value
     * @throws Exception in case of error
     */
    public double classifyInstance(int i, Instance obj) throws Exception {
        return classifiers.get(i).classifyInstance(obj);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param i   index of the classifier
     * @param obj instance object
     * @return the array of classes probabilities
     * @throws Exception in case of error
     */
    public double[] distributionForInstance(int i, Instance obj) throws Exception {
        return classifiers.get(i).distributionForInstance(obj);
    }

    /**
     * Aggregate classification results of individual models
     * using majority votes method.
     *
     * @param obj instance object
     * @return class value
     * @throws Exception in case of error
     */
    public double aggregate(Instance obj) throws Exception {
        return aggregate(obj, null);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param obj instance object
     * @return the array of classes probabilities
     * @throws Exception in case of error
     */
    public double[] distributionForInstance(Instance obj) throws Exception {
        return distributionForInstance(obj, null);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param obj     - instance object
     * @param weights - classifiers weight
     * @return the array of classes probabilities
     * @throws Exception in case of error
     */
    public double[] distributionForInstance(Instance obj, List<Double> weights) throws Exception {
        double[] sums;
        if (classifiers.size() == 1) {
            return classifiers.get(0).distributionForInstance(obj);
        } else if (weights == null) {
            sums = new double[instances.numClasses()];
            for (int i = 0; i < classifiers.size(); i++) {
                double[] distr = distributionForInstance(i, obj);
                for (int j = 0; j < distr.length; j++) {
                    sums[j] += distr[j];
                }
            }
        } else {
            sums = getVoices(obj, weights);
        }
        Utils.normalize(sums);
        return sums;
    }

    /**
     * Returns the voices array for given instance.
     *
     * @param obj     -instance object
     * @param weights -classifiers weight
     * @return the voices array for given instance
     * @throws Exception in case of error
     */
    public double[] getVoices(Instance obj, List<Double> weights) throws Exception {
        double[] voices = new double[obj.numClasses()];
        for (int i = 0; i < classifiers.size(); i++) {
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
     * @throws Exception in case of error
     */
    public double aggregate(Instance obj, List<Double> weights) throws Exception {
        Objects.requireNonNull(obj, "Instance isn't specified!");
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
