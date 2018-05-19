/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * Implements collection of individual classifiers models.
 *
 * @author Roman Batygin
 */
public class ClassifiersSet implements java.lang.Iterable<Classifier>, java.io.Serializable {

    private ArrayList<Classifier> classifiers = new ArrayList<>();

    /**
     * Default constructor.
     */
    public ClassifiersSet() {

    }

    /**
     * Creates classifiers copies.
     *
     * @param another another object
     * @throws Exception
     */
    public ClassifiersSet(ClassifiersSet another) throws Exception {
        this.classifiers = new ArrayList<>(another.size());
        for (int i = 0; i < another.size(); i++) {
            addClassifier(another.getClassifierCopy(i));
        }
    }

    /**
     * Adds classifier to collection.
     *
     * @param model <tt>Classifier</tt> object
     * @return <tt>true</tt> if this collection changed as a result of the call
     */
    public boolean addClassifier(Classifier model) {
        return model != null && classifiers.add(model);
    }

    /**
     * Returns <tt>Classifier</tt> object at the specified position in this collection.
     *
     * @param i index of element
     * @return <tt>Classifier</tt> object at the specified position in this collection
     */
    public Classifier getClassifier(int i) {
        return classifiers.get(i);
    }

    /**
     * Replaces the classifier at the specified position in this collection with
     * the specified classifier.
     *
     * @param i index of the element to replace
     * @param c <tt>Classifier</tt> object
     */
    public void setClassifier(int i, Classifier c) {
        classifiers.set(i, c);
    }

    /**
     * Returns classifier model at the specified position in this collection
     * built on given training set.
     *
     * @param i    index of the element
     * @param data <tt>Instances</tt> object
     * @return classifier model at the specified position in this collection
     * built on given training set
     * @throws Exception
     */
    public Classifier buildClassifier(int i, Instances data) throws Exception {
        Classifier classifier = classifiers.get(i);
        classifier.buildClassifier(data);
        return classifier;
    }

    /**
     * Returns <tt>Classifier</tt> object copy at the specified position in this collection.
     *
     * @param i index of the element
     * @return <tt>Classifier</tt> object copy at the specified position in this collection
     * @throws Exception
     */
    public Classifier getClassifierCopy(int i) throws Exception {
        return AbstractClassifier.makeCopy(classifiers.get(i));
    }

    /**
     * Returns the number of classifiers in this collection.
     *
     * @return the number of classifiers in this collection
     */
    public int size() {
        return classifiers.size();
    }

    /**
     * Returns <tt>true</tt> if this collection contains no elements.
     *
     * @return <tt>true</tt> if this collection contains no elements
     */
    public boolean isEmpty() {
        return classifiers.isEmpty();
    }

    /**
     * Removes all of the elements from this collection.
     */
    public void clear() {
        classifiers.clear();
    }

    @Override
    public Iterator<Classifier> iterator() {
        return classifiers.iterator();
    }

    /**
     * Returns <tt>Classifier</tt> object at random position.
     *
     * @param random {@link Random} object
     * @return {@link Classifier} object at random position
     * @throws Exception
     */
    public Classifier randomClassifier(Random random) throws Exception {
        return isEmpty() ? null : AbstractClassifier.makeCopy(classifiers.get(random.nextInt(size())));
    }

    /**
     * Returns classifier model at random position in this collection
     * built on given training set.
     *
     * @param data   {@link Instances} object
     * @param random {@link Random} object
     * @return classifier model at random position in this collection
     * built on given training set
     * @throws Exception
     */
    public Classifier buildRandomClassifier(Instances data, Random random) throws Exception {
        Classifier classifier = randomClassifier(random);
        classifier.buildClassifier(data);
        return classifier;
    }

    /**
     * Returns classifier model in this collection minimizing
     * classification error on given training set.
     *
     * @param data <tt>Instances</tt> object
     * @return classifier model in this collection minimizing
     * classification error on given training set
     * @throws Exception
     */
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

    /**
     * Returns classifiers set converted to <tt>ArrayList</tt> object.
     *
     * @return classifiers set converted to <tt>ArrayList</tt> object
     */
    public List<Classifier> toList() {
        return Collections.unmodifiableList(classifiers);
    }

}
