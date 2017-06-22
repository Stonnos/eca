/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import eca.core.evaluation.Evaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 *
 * @author Рома
 */
public class ClassifiersSet implements java.lang.Iterable<Classifier>, java.io.Serializable, Cloneable {

    private ArrayList<Classifier> classifiers = new ArrayList<>();
    private Random r = new Random();

    public boolean addClassifier(Classifier model) {
        return model == null ? false : classifiers.add(model);
    }

    public Classifier getClassifier(int i) {
        return classifiers.get(i);
    }
    
    public void setClassifier(int i, Classifier c) {
        classifiers.set(i, c);
    }

    public Classifier buildClassifier(int i, Instances data) throws Exception {
        Classifier classifier = classifiers.get(i);
        classifier.buildClassifier(data);
        return classifier;
    }

    public Classifier getClassifierCopy(int i) throws Exception {
        return AbstractClassifier.makeCopy(classifiers.get(i));
    }

    public int size() {
        return classifiers.size();
    }

    public boolean isEmpty() {
        return classifiers.isEmpty();
    }

    public void clear() {
        classifiers.clear();
    }

    @Override
    public Iterator<Classifier> iterator() {
        return classifiers.iterator();
    }

    @Override
    public ClassifiersSet clone() {
        ClassifiersSet clone;
        try {
            clone = (ClassifiersSet) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        clone.classifiers = (ArrayList<Classifier>) classifiers.clone();
        try {
            for (int i = 0; i < clone.classifiers.size(); i++) {
                clone.classifiers.set(i, getClassifierCopy(i));
            }
        } catch (Exception e) {
            throw new InternalError(e);
        }
        return clone;
    }

    public Classifier randomClassifier() throws Exception {
        return isEmpty() ? null : AbstractClassifier.makeCopy(classifiers.get(r.nextInt(size())));
    }

    public Classifier buildRandomClassifier(Instances data) throws Exception {
        Classifier classifier = randomClassifier();
        classifier.buildClassifier(data);
        return classifier;
    }

    public Classifier builtOptimalClassifier(Instances data) throws Exception {
        Classifier model = null;
        double minError = Double.MAX_VALUE;
        for (int i = 0; i < size(); i++) {
            Classifier c = getClassifierCopy(i);
            c.buildClassifier(data);
            double error = Evaluation.error(c, data);
            if (error < minError) {
                minError = error;
                model = c;
            }
        }
        return model;
    }

    public ArrayList<Classifier> toList() {
        return classifiers;
    }

}
