package eca.gui.frames.results;

import eca.gui.frames.results.model.ComponentModel;
import eca.gui.frames.results.provider.DecisionTreeComponentsProvider;
import eca.gui.frames.results.provider.EnsembleClassifierComponentsProvider;
import eca.gui.frames.results.provider.EvaluationResultsComponentsProvider;
import eca.gui.frames.results.provider.J48ComponentsProvider;
import eca.gui.frames.results.provider.LogisticComponentsProvider;
import eca.gui.frames.results.provider.NeuralNetworksComponentsProvider;
import lombok.experimental.UtilityClass;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Evaluation results components factory.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EvaluationResultsComponentsFactory {

    private static List<EvaluationResultsComponentsProvider> evaluationResultsComponentsProviders;

    static {
        evaluationResultsComponentsProviders = newArrayList();
        evaluationResultsComponentsProviders.add(new DecisionTreeComponentsProvider());
        evaluationResultsComponentsProviders.add(new NeuralNetworksComponentsProvider());
        evaluationResultsComponentsProviders.add(new LogisticComponentsProvider());
        evaluationResultsComponentsProviders.add(new EnsembleClassifierComponentsProvider());
        evaluationResultsComponentsProviders.add(new J48ComponentsProvider());
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
    public static List<ComponentModel> getComponents(Classifier classifier, Instances data, int maxFractionDigits,
                                                     JFrame parent) throws Exception {
        EvaluationResultsComponentsProvider provider = evaluationResultsComponentsProviders.stream()
                .filter(p -> p.canHandle(classifier))
                .findFirst()
                .orElse(null);
        return provider != null ? provider.getComponents(classifier, data, maxFractionDigits, parent) :
                Collections.emptyList();
    }
}
