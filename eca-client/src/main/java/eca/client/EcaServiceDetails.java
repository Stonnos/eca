package eca.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Eca - service details model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EcaServiceDetails {

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

    /**
     * User login
     */
    private String userName;

    /**
     * User password
     */
    private String password;
}
