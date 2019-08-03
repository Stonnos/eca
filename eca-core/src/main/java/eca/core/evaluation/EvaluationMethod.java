package eca.core.evaluation;

import eca.core.DescriptiveEnum;

/**
 * Classifiers evaluation type.
 *
 * @author Roman Batygin
 */
public enum EvaluationMethod implements DescriptiveEnum {

    /**
     * Use training data
     **/
    TRAINING_DATA(EvaluationMethodDictionary.INITIAL_METHOD_TITLE) {
        @Override
        public void accept(EvaluationMethodVisitor evaluationMethodVisitor) throws Exception {
            evaluationMethodVisitor.evaluateModel();
        }
    },

    /**
     * Use k * V - folds cross - validation method
     **/
    CROSS_VALIDATION(EvaluationMethodDictionary.CV_METHOD_TITLE) {
        @Override
        public void accept(EvaluationMethodVisitor evaluationMethodVisitor) throws Exception {
            evaluationMethodVisitor.crossValidateModel();
        }
    };

    private String description;

    EvaluationMethod(String description) {
        this.description = description;
    }

    /**
     * Returns evaluation method description.
     *
     * @return evaluation method description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Visitor pattern common method
     *
     * @param evaluationMethodVisitor visitor class
     */
    public abstract void accept(EvaluationMethodVisitor evaluationMethodVisitor) throws Exception;
}
