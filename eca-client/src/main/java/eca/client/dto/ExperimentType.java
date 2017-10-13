package eca.client.dto;

import eca.dictionary.ClassifiersNamesDictionary;
import eca.dictionary.EnsemblesNamesDictionary;

/**
 * Experiment type.
 *
 * @author Roman Batygin
 */
public enum ExperimentType {

    /**
     * Optimal options automatic selection for neural networks.
     */
    NEURAL_NETWORKS(ClassifiersNamesDictionary.NEURAL_NETWORK),

    /**
     * Optimal options automatic selection for heterogeneous ensemble algorithm.
     */
    HETEROGENEOUS_ENSEMBLE(EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE),

    /**
     * Optimal options automatic selection for modified heterogeneous ensemble algorithm.
     */
    MODIFIED_HETEROGENEOUS_ENSEMBLE(EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE),

    /**
     * Optimal options automatic selection for AdaBoost algorithm.
     */
    ADA_BOOST(EnsemblesNamesDictionary.BOOSTING),

    /**
     * Optimal options automatic selection for stacking algorithm.
     */
    STACKING(EnsemblesNamesDictionary.STACKING),

    /**
     * Optimal options automatic selection for k - nearest neighbours algorithm.
     */
    KNN(ClassifiersNamesDictionary.KNN);

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
