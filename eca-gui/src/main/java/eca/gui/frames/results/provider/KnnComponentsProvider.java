package eca.gui.frames.results.provider;

import eca.gui.frames.results.model.ComponentModel;
import eca.metrics.KNearestNeighbours;
import weka.core.Instances;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class KnnComponentsProvider extends EvaluationResultsComponentsProvider<KNearestNeighbours> {

    public KnnComponentsProvider() {
        super(KNearestNeighbours.class);
    }

    @Override
    public List<ComponentModel> getComponents(KNearestNeighbours classifier,
                                              Instances data,
                                              int maxFractionDigits,
                                              JFrame parent) {
        return Collections.emptyList();
    }
}
