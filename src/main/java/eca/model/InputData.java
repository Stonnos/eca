package eca.model;

import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.Serializable;

/**
 * Classifier input options model.
 * @author Roman Batygin
 */

public class InputData implements Serializable {

    private AbstractClassifier classifier;

    private Instances data;

    public InputData(AbstractClassifier classifier, Instances data) {
        this.classifier = classifier;
        this.data = data;
    }

    public AbstractClassifier getClassifier() {
        return classifier;
    }

    public void setClassifier(AbstractClassifier classifier) {
        this.classifier = classifier;
    }

    public Instances getData() {
        return data;
    }

    public void setData(Instances data) {
        this.data = data;
    }
}
