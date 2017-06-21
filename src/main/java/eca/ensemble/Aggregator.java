/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instance;
import java.util.ArrayList;
import weka.core.Utils;
/**
 *
 * @author Рома
 */
public class Aggregator implements java.io.Serializable {
    
    private IterativeEnsembleClassifier classifier;
    
    public Aggregator(IterativeEnsembleClassifier classifier) {
        this.classifier = classifier;
    }
    
    public IterativeEnsembleClassifier classifier() {
        return classifier;
    }
    
    public double classifyInstance(int i, Instance obj) throws Exception {
        return classifier.classifiers.get(i).classifyInstance(obj);
    }
    
    public double[] distributionForInstance(int i, Instance obj) throws Exception {
        return classifier.classifiers.get(i).distributionForInstance(obj);
    }
    
    public double aggregate(Instance obj) throws Exception {
        return aggregate(obj, null); 
    }
    
    public double[] distributionForInstance(Instance obj) throws Exception {
        return distributionForInstance(obj, null);
    }

    public double[] distributionForInstance(Instance obj, ArrayList<Double> weights) throws Exception {
        double[] sums;
        if (classifier.classifiers.size() == 1) {
            return classifier.classifiers.get(0).distributionForInstance(obj);
        }
        else if (weights == null) {
            sums = new double[classifier.data.numClasses()];
            for (int i = 0; i < classifier.classifiers.size(); i++) {
                double[] distr = distributionForInstance(i, obj);
                for (int j = 0; j < distr.length; j++)
                    sums[j] += distr[j];
            }
        }
        else {
            sums = getVoices(obj, weights);
        }       
        if (Utils.eq(Utils.sum(sums), 0)) {
            return sums;
        }
        else Utils.normalize(sums);
        return sums;
    }
    
    public double[] getVoices(Instance obj, ArrayList<Double> weights) throws Exception {
        double[] voices = new double[obj.numClasses()];
        for (int i = 0; i < classifier.classifiers.size(); i++) {
            int classIndex = (int)classifyInstance(i, obj);
            if (weights == null) voices[classIndex]++; else voices[classIndex] += weights.get(i);
        }     
        return voices;
    }
    
    public double aggregate(Instance obj, ArrayList<Double> weights) throws Exception {
        if (obj == null)
            throw new NullPointerException();
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
