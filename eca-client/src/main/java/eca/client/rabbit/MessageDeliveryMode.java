package eca.client.rabbit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Message delivery mode.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum MessageDeliveryMode {

    /**
     * Not persistent mode
     */
    NOT_PERSISTENT(1),

    /**
     * Persistent mode
     */
    PERSISTENT(2);

    private final int code;
}
