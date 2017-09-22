package eca.core;

/**
 * Classifiers evaluation type.
 *
 * @author Roman Batygin
 */
public enum EvaluationMethod {

    /**
     * Use training data
     **/
    TRAINING_DATA {
        @Override
        public <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor) {
            return evaluationMethodVisitor.evaluateModel();
        }
    },

    /**
     * Use k * V - folds cross - validation method
     **/
    CROSS_VALIDATION {
        @Override
        public <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor) {
            return evaluationMethodVisitor.crossValidateModel();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param evaluationMethodVisitor visitor class
     * @param <T>                     generic class
     * @return generic class
     */
    public abstract <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor);
}
