package eca.dataminer;

import eca.core.evaluation.EvaluationResults;

import java.util.Comparator;

/**
 * Class for comparing classifiers accuracy.
 *
 * @author Roman Batygin
 */
public class ClassifierComparator implements Comparator<EvaluationResults> {

    @Override
    public int compare(EvaluationResults c1, EvaluationResults c2) {
        int compare = -Double.compare(c1.getEvaluation().pctCorrect(), c2.getEvaluation().pctCorrect());
        if (compare == 0) {
            return -Double.compare(c1.getEvaluation().maxAreaUnderROC(), c2.getEvaluation().maxAreaUnderROC());
        } else {
            return compare;
        }
    }
}
