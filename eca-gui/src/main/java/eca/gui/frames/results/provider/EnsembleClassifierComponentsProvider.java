package eca.gui.frames.results.provider;

import eca.ensemble.EnsembleClassifier;
import eca.gui.frames.results.model.ComponentModel;
import eca.gui.tables.EnsembleTable;
import weka.core.Instances;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class EnsembleClassifierComponentsProvider extends EvaluationResultsComponentsProvider<EnsembleClassifier> {

    private static final String ENSEMBLE_STRUCTURE_TAB_TITLE = "Структура ансамбля";

    public EnsembleClassifierComponentsProvider() {
        super(EnsembleClassifier.class);
    }

    @Override
    public List<ComponentModel> getComponents(EnsembleClassifier classifier,
                                              Instances data,
                                              int maxFractionDigits,
                                              JFrame parent) throws Exception {
        EnsembleTable ensembleClassifierStructureTable =
                new EnsembleTable(classifier.getStructure(), parent, maxFractionDigits);
        JScrollPane scrollPane = new JScrollPane(ensembleClassifierStructureTable);
        ComponentModel componentModel = new ComponentModel(ENSEMBLE_STRUCTURE_TAB_TITLE, scrollPane);
        return Collections.singletonList(componentModel);
    }
}
