package eca.gui.frames.results.provider;

import eca.gui.frames.results.model.ComponentModel;
import eca.trees.DecisionTreeClassifier;
import eca.trees.TreeVisualizer;
import weka.core.Instances;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class DecisionTreeComponentsProvider extends EvaluationResultsComponentsProvider<DecisionTreeClassifier> {

    private static final String TREE_STRUCTURE_TAB_TITLE = "Структура дерева";

    public DecisionTreeComponentsProvider() {
        super(DecisionTreeClassifier.class);
    }

    @Override
    public List<ComponentModel> getComponents(DecisionTreeClassifier classifier,
                                              Instances data,
                                              int maxFractionDigits,
                                              JFrame parent) {
        JScrollPane scrollPane = new JScrollPane(new TreeVisualizer(classifier, maxFractionDigits));
        JScrollBar bar = scrollPane.getHorizontalScrollBar();
        bar.setValue(bar.getMaximum());
        ComponentModel componentModel = new ComponentModel(TREE_STRUCTURE_TAB_TITLE, scrollPane);
        return Collections.singletonList(componentModel);
    }
}
