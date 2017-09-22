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
        int compare = -Double.valueOf(c1.getEvaluation().pctCorrect()).compareTo(c2.getEvaluation().pctCorrect());
        if (compare == 0) {
            return -Double.valueOf(c1.getEvaluation().maxAreaUnderROC()).compareTo(
                    c2.getEvaluation().maxAreaUnderROC());
        } else {
            return compare;
        }
    }
}
