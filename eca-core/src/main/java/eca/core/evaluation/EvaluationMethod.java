package eca.core.evaluation;

/**
 * Classifiers evaluation type.
 *
 * @author Roman Batygin
 */
public enum EvaluationMethod {

    /**
     * Use training data
     **/
    TRAINING_DATA(EvaluationMethodDictionary.INITIAL_METHOD_TITLE) {
        @Override
        public <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor) {
            return evaluationMethodVisitor.evaluateModel();
        }
    },

    /**
     * Use k * V - folds cross - validation method
     **/
    CROSS_VALIDATION(EvaluationMethodDictionary.CV_METHOD_TITLE) {
        @Override
        public <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor) {
            return evaluationMethodVisitor.crossValidateModel();
        }
    };

    private String description;

    EvaluationMethod(String description) {
        this.description = description;
    }

    /**
     * Returns evaluation method description.
     * @return evaluation method description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Visitor pattern common method
     *
     * @param evaluationMethodVisitor visitor class
     * @param <T>                     generic class
     * @return generic class
     */
    public abstract <T> T accept(EvaluationMethodVisitor<T> evaluationMethodVisitor);
}
