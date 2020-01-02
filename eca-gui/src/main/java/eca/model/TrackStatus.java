package eca.model;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Eca - service track status.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum TrackStatus implements DescriptiveEnum {

    /**
     * Request sent status
     */
    REQUEST_SENT("Запрос отправлен"),

    /**
     * Response received status
     */
    RESPONSE_RECEIVED("Получен ответ");


    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
