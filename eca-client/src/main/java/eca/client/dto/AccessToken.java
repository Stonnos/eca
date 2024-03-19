package eca.client.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Access token model.
 *
 * @author Roman Batygin
 */
@Data
@RequiredArgsConstructor
public class AccessToken {

    /**
     * Access token value
     */
    private final String accessToken;
    /**
     * Token type
     */
    private final String tokenType;
    /**
     * Token expiration date
     */
    private final LocalDateTime expiresAt;

    /**
     * Is token expired?
     *
     * @return {@code true} if token is expiredm otherwise {@code false}
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
