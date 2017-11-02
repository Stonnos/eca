/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

/**
 * Abstract class for generating heterogeneous classifier model.
 *
 * @author Roman Batygin
 */
public abstract class AbstractHeterogeneousClassifier extends ThresholdClassifier {

    /**
     * Classifiers set
     **/
    private ClassifiersSet set;

    protected AbstractHeterogeneousClassifier(ClassifiersSet set) {
        this.set = set;
    }

    protected AbstractHeterogeneousClassifier() {

    }

    /**
     * Returns classifiers collection.
     *
     * @return classifiers collection
     */
    public ClassifiersSet getClassifiersSet() {
        return set;
    }

    /**
     * Sets classifiers collection.
     *
     * @param set <tt>ClassifiersSet</tt> object
     */
    public void setClassifiersSet(ClassifiersSet set) {
        this.set = set;
    }

}
