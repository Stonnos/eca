package eca.experiment;

import eca.beans.ClassifierDescriptor;

import java.util.Comparator;

/**
 * @author Roman Batygin
 */
public class ClassifierComparator implements Comparator<ClassifierDescriptor> {

    @Override
    public int compare(ClassifierDescriptor c1, ClassifierDescriptor c2) {
        int compare = -Double.valueOf(c1.getEvaluation().pctCorrect()).compareTo(c2.getEvaluation().pctCorrect());
        if (compare == 0) {
            return -Double.valueOf(c1.getEvaluation().maxAreaUnderROC()).compareTo(c2.getEvaluation().maxAreaUnderROC());
        }
        else return compare;
    }
}
