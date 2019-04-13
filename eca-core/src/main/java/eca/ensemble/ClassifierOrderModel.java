package eca.ensemble;

import lombok.Data;
import weka.classifiers.Classifier;

import java.io.Serializable;

/**
 * Classifier order model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOrderModel implements Serializable {

    /**
     * Classifier model
     */
    private Classifier classifier;

    /**
     * Classifier order
     */
    private int order;

    /**
     * Classifier weight
     */
    private Double weight;

    public ClassifierOrderModel() {
    }

    public ClassifierOrderModel(Classifier classifier, int order) {
        this.classifier = classifier;
        this.order = order;
    }

    public ClassifierOrderModel(Classifier classifier, int order, Double weight) {
        this.classifier = classifier;
        this.order = order;
        this.weight = weight;
    }
}
