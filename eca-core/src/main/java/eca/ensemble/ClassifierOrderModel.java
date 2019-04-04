package eca.ensemble;

import lombok.Builder;
import lombok.Data;
import weka.classifiers.Classifier;

/**
 * Classifier order model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class ClassifierOrderModel {

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
