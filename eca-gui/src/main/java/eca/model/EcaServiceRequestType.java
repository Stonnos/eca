package eca.model;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Eca - service request type.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum EcaServiceRequestType implements DescriptiveEnum {

    /**
     * Classifier request type
     */
    CLASSIFIER("Построение классификатора"),

    /**
     * Experiment request
     */
    EXPERIMENT("Заявка на эксперимент"),

    /**
     * Optimal classifier request
     */
    OPTIMAL_CLASSIFIER("Подбор оптимальных параметров классификатора");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
