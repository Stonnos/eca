package eca.config;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.nio.charset.StandardCharsets;

/**
 * Velocity configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class VelocityConfigService {

    private static final String CLASSPATH_PROPERTY = "classpath";
    private static final String CLASSPATH_RESOURCE_LOADER_PROPERTY = "classpath.resource.loader.class";

    private static VelocityEngine velocityEngine;

    static {
        try {
            velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, CLASSPATH_PROPERTY);
            velocityEngine.setProperty(CLASSPATH_RESOURCE_LOADER_PROPERTY, ClasspathResourceLoader.class.getName());
            velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
            velocityEngine.init();
        } catch (Exception ex) {
            log.error("Error in init velocity engine: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Returns velocity template.
     *
     * @param name velocity template name
     * @return {@link Template} object
     */
    public static Template getTemplate(String name) {
        return velocityEngine.getTemplate(name, StandardCharsets.UTF_8.name());
    }
}
