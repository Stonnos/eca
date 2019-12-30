package eca.gui.frames.results.provider;

import eca.gui.frames.results.model.ComponentModel;
import eca.neural.NetworkVisualizer;
import eca.neural.NeuralNetwork;
import weka.core.Instances;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class NeuralNetworksComponentsProvider extends EvaluationResultsComponentsProvider<NeuralNetwork> {

    private static final String NETWORK_STRUCTURE_TAB_TITLE = "Структура нейронной сети";

    public NeuralNetworksComponentsProvider() {
        super(NeuralNetwork.class);
    }

    @Override
    public List<ComponentModel> getComponents(NeuralNetwork classifier, Instances data, int maxFractionDigits,
                                              JFrame parent) {
        JScrollPane scrollPane = new JScrollPane(new NetworkVisualizer(classifier, parent, maxFractionDigits));
        ComponentModel componentModel = new ComponentModel(NETWORK_STRUCTURE_TAB_TITLE, scrollPane);
        return Collections.singletonList(componentModel);
    }
}
