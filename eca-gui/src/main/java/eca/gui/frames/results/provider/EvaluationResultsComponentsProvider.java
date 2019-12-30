package eca.gui.frames.results.provider;

import eca.gui.frames.results.model.ComponentModel;
import lombok.RequiredArgsConstructor;
import weka.core.Instances;

import javax.swing.*;
import java.util.List;

/**
 * Classifier evaluation results components provider.
 *
 * @param <T> - classifier generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public abstract class EvaluationResultsComponentsProvider<T> {

    private final Class<T> classifierClazz;

    public boolean canHandle(T classifier) {
        return classifierClazz.isAssignableFrom(classifier.getClass());
    }

    /**
     * Gets components list with results for specified classifier.
     *
     * @param classifier        - classifier object
     * @param data              - training data
     * @param maxFractionDigits - max fraction digits
     * @param parent            - parent frame
     * @return components list
     * @throws Exception in case of error
     */
    public abstract List<ComponentModel> getComponents(T classifier, Instances data, int maxFractionDigits,
                                                       JFrame parent) throws Exception;
}
