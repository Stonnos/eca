package eca.core;

/**
 * @author Roman Batygin
 */
public enum EvaluationMethod {

    TRAINING_DATA {

        @Override
        public <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor) {
            return evaluationMethodVisitor.evaluateModel();
        }
    },

    CROSS_VALIDATION {

        @Override
        public <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor) {
            return evaluationMethodVisitor.crossValidateModel();
        }
    };


    public abstract <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor);
}
