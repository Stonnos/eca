package eca.gui.frames.results;

import eca.core.evaluation.Evaluation;
import eca.gui.frames.results.model.ComponentModel;
import eca.gui.tables.EvaluationStatisticsTableFactory;
import eca.gui.tables.models.EvaluationStatisticsModel;
import eca.model.ReferenceWrapper;
import lombok.experimental.UtilityClass;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import java.util.List;

/**
 * Classification results frame factory.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ClassificationResultsFrameFactory {

    /**
     * Creates classification results frame.
     *
     * @param parent            - parent frame
     * @param title             - title string
     * @param classifier        - classifier object
     * @param data              - training data
     * @param evaluation        - evaluation object
     * @param maxFractionDigits - maximum fraction digits
     * @return classification results frame
     * @throws Exception in case of error
     */
    public static ClassificationResultsFrameBase buildClassificationResultsFrameBase(JFrame parent, String title,
                                                                                     ReferenceWrapper<Classifier> classifier,
                                                                                     Instances data,
                                                                                     Evaluation evaluation,
                                                                                     int maxFractionDigits)
            throws Exception {
        ClassificationResultsFrameBase classificationResultsFrameBase =
                new ClassificationResultsFrameBase(parent, title, classifier, data, evaluation, maxFractionDigits);
        EvaluationStatisticsModel evaluationStatisticsTable =
                EvaluationStatisticsTableFactory.buildEvaluationStatisticsTable(classifier.getItem(),
                        evaluation, maxFractionDigits);
        classificationResultsFrameBase.setEvaluationStatisticsModel(evaluationStatisticsTable);
        List<ComponentModel> componentModels =
                EvaluationResultsComponentsFactory.getComponents(classifier.getItem(), data, maxFractionDigits,
                        classificationResultsFrameBase);
        classificationResultsFrameBase.setEvaluationResultsComponents(componentModels);
        return classificationResultsFrameBase;
    }
}
