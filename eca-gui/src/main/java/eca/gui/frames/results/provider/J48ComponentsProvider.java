package eca.gui.frames.results.provider;

import eca.gui.frames.results.model.ComponentModel;
import eca.trees.J48;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class J48ComponentsProvider extends EvaluationResultsComponentsProvider<J48> {

    private static final String TREE_STRUCTURE_TAB_TITLE = "Структура дерева";

    public J48ComponentsProvider() {
        super(J48.class);
    }

    @Override
    public List<ComponentModel> getComponents(J48 classifier,
                                              Instances data,
                                              int maxFractionDigits,
                                              JFrame parent) throws Exception {
        ComponentModel componentModel = new ComponentModel(TREE_STRUCTURE_TAB_TITLE,
                new weka.gui.treevisualizer.TreeVisualizer(null, classifier.graph(), new PlaceNode2()));
        return Collections.singletonList(componentModel);
    }
}
