/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.Assert;
import eca.util.Utils;
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
     * Default weight
     */
    private static final double DEFAULT_WEIGHT = 1d;

    /**
     * Iterative ensemble model
     **/
    private List<ClassifierOrderModel> classifiers;

    /**
     * Instances model
     */
    private Instances instances;

    /**
     * Creates aggregator object.
     *
     * @param classifiers - classifiers list
     */
    public Aggregator(List<ClassifierOrderModel> classifiers, Instances instances) {
        this.classifiers = classifiers;
        this.instances = instances;
    }

    /**
     * Gets classifiers list.
     *
     * @return classifiers list
     */
    public List<ClassifierOrderModel> getClassifiers() {
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
     * @param i        - index of the classifier
     * @param instance - instance object
     * @return class value
     * @throws Exception in case of error
     */
    public double classifyInstance(int i, Instance instance) throws Exception {
        return classifiers.get(i).getClassifier().classifyInstance(instance);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param i        - index of the classifier
     * @param instance - instance object
     * @return the array of classes probabilities
     * @throws Exception in case of error
     */
    public double[] distributionForInstance(int i, Instance instance) throws Exception {
        return classifiers.get(i).getClassifier().distributionForInstance(instance);
    }

    /**
     * Returns the array of classes probabilities.
     *
     * @param instance   - instance object
     * @param useWeights - is use classifiers weights
     * @return the array of classes probabilities
     * @throws Exception in case of error
     */
    public double[] distributionForInstance(Instance instance, boolean useWeights) throws Exception {
        double[] sums;
        if (classifiers.size() == 1) {
            return classifiers.get(0).getClassifier().distributionForInstance(instance);
        } else if (!useWeights) {
            sums = new double[instances.numClasses()];
            for (int i = 0; i < classifiers.size(); i++) {
                double[] distributionForInstance = distributionForInstance(i, instance);
                for (int j = 0; j < distributionForInstance.length; j++) {
                    sums[j] += distributionForInstance[j];
                }
            }
        } else {
            sums = getVoices(instance, true);
        }
        Utils.normalize(sums);
        return sums;
    }

    /**
     * Returns the voices array for given instance.
     *
     * @param instance   -instance object
     * @param useWeights - is use classifiers weights
     * @return the voices array for given instance
     * @throws Exception in case of error
     */
    public double[] getVoices(Instance instance, boolean useWeights) throws Exception {
        double[] voices = new double[instance.numClasses()];
        for (int i = 0; i < classifiers.size(); i++) {
            int classIndex = (int) classifyInstance(i, instance);
            double weight;
            if (useWeights) {
                ClassifierOrderModel classifierOrderModel = classifiers.get(i);
                checkClassifierWeight(classifierOrderModel, i);
                weight = classifierOrderModel.getWeight();
            } else {
                weight = DEFAULT_WEIGHT;
            }
            voices[classIndex] += weight;
        }
        return voices;
    }

    /**
     * Aggregate classification results of individual models
     * using weighted votes method.
     *
     * @param instance   - instance object
     * @param useWeights - is use classifiers weights
     * @return class value
     * @throws Exception in case of error
     */
    public double aggregate(Instance instance, boolean useWeights) throws Exception {
        double[] voices = getVoices(instance, useWeights);
        return classValue(voices);
    }

    private void checkClassifierWeight(ClassifierOrderModel classifierOrderModel, int classifierIndex) {
        Objects.requireNonNull(classifierOrderModel.getWeight(),
                String.format("Weight isn't specified for classifier [%s] at index [%d]!",
                        classifierOrderModel.getClassifier().getClass().getSimpleName(), classifierIndex));
        Assert.notNegative(classifierOrderModel.getWeight(),
                String.format("Found negative weight value for classifier [%s] at index [%d]!",
                        classifierOrderModel.getClassifier().getClass().getSimpleName(), classifierIndex));
    }

    private double classValue(double[] voices) {
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
