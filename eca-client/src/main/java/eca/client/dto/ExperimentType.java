package eca.client.dto;

import eca.client.dictionary.ExperimentTypeDictionary;

/**
 * Experiment type.
 *
 * @author Roman Batygin
 */
public enum ExperimentType {

    /**
     * Optimal options automatic selection for neural networks.
     */
    NEURAL_NETWORKS(ExperimentTypeDictionary.NEURAL_NETWORK_NAME),

    /**
     * Optimal options automatic selection for heterogeneous ensemble algorithm.
     */
    HETEROGENEOUS_ENSEMBLE(ExperimentTypeDictionary.HETEROGENEOUS_ENSEMBLE_NAME),

    /**
     * Optimal options automatic selection for modified heterogeneous ensemble algorithm.
     */
    MODIFIED_HETEROGENEOUS_ENSEMBLE(ExperimentTypeDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE_NAME),

    /**
     * Optimal options automatic selection for AdaBoost algorithm.
     */
    ADA_BOOST(ExperimentTypeDictionary.ADA_BOOST_NAME),

    /**
     * Optimal options automatic selection for stacking algorithm.
     */
    STACKING(ExperimentTypeDictionary.STACKING_NAME),

    /**
     * Optimal options automatic selection for k - nearest neighbours algorithm.
     */
    KNN(ExperimentTypeDictionary.KNN_NAME),

    /**
     * Optimal options automatic selection for Random forests algorithm.
     */
    RANDOM_FORESTS(ExperimentTypeDictionary.RANDOM_FORESTS_NAME),

    /**
     * Optimal options automatic selection for stacking algorithm using cross - validation
     * method for meta data set creation.
     */
    STACKING_CV(ExperimentTypeDictionary.STACKING_CV_NAME),

    /**
     * Optimal options automatic selection for decision tree algorithms.
     */
    DECISION_TREE(ExperimentTypeDictionary.DECISION_TREE_NAME);

    private String description;

    ExperimentType(String description) {
        this.description = description;
    }

    /**
     * Returns experiment type description.
     *
     * @return experiment type description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns experiment type description.
     * @return experiment type description
     */
    public static String[] getDescriptions() {
        ExperimentType[] values = values();
        String[] descriptions = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            descriptions[i] = values[i].getDescription();
        }
        return descriptions;
    }

    /**
     * Finds experiment type type by description
     *
     * @param description description string.
     * @return {@link ExperimentType} object
     */
    public static ExperimentType findByDescription(String description) {
        for (ExperimentType experimentType : values()) {
            if (experimentType.getDescription().equals(description)) {
                return experimentType;
            }
        }
        return null;
    }
}
