package eca.config;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Rest template configuration.
 * @author Roman Batygin
 */

public class RestTemplateConfig {

    /**
     * Creates rest template object.
     * @return {@link RestTemplate} object
     */
    public static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }
}
