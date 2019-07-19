package eca.config;

import lombok.Data;

/**
 * Eca - service api config.
 *
 * @author Roman Batygin
 */
@Data
public class EcaServiceConfig {

    /**
     * Eca - service enabled?
     */
    private Boolean enabled;

    /**
     * Api url
     */
    private String apiUrl;

    /**
     * Token url
     */
    private String tokenUrl;

    /**
     * Application id
     */
    private String clientId;

    /**
     * Application secret
     */
    private String clientSecret;
}
