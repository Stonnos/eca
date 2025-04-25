package eca.model;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Eca - service track status.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum EcaServiceTrackStatus implements DescriptiveEnum {

    /**
     * Ready to sent
     */
    READY("Готов к отправке"),

    /**
     * Request sent status
     */
    REQUEST_SENT("Запрос отправлен"),

    /**
     * Request in progress
     */
    IN_PROGRESS("В обработке"),

    /**
     * Completed status
     */
    COMPLETED("Успешно завершен"),

    /**
     * Error status
     */
    ERROR("Ошибка"),

    /**
     * Timeout status
     */
    TIMEOUT("Таймаут"),

    /**
     * Canceled status
     */
    CANCELED("Прервано");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
