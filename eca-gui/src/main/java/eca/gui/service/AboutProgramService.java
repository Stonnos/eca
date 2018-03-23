package eca.gui.service;

import eca.config.ApplicationProperties;
import eca.config.VelocityConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;

/**
 * About program service class.
 *
 * @author Roman Batygin
 */
@Slf4j
public class AboutProgramService {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String AUTHOR = "author";
    private static final String EMAIL = "email";
    private static final String VERSION = "version";
    private static final String LAST_UPDATE_DATE = "lastUpdateDate";

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    private static final VelocityConfiguration VELOCITY_CONFIGURATION =
            VelocityConfiguration.getVelocityConfiguration();

    private static final String TEMPLATE_FILE_NAME = "vm-templates/aboutProgramTemplate.vm";

    private static String aboutProgramHtmlString;

    static {
        try {
            Template template = VELOCITY_CONFIGURATION.getTemplate(TEMPLATE_FILE_NAME);
            VelocityContext context = new VelocityContext();
            context.put(TITLE, APPLICATION_PROPERTIES.getTitle());
            context.put(DESCRIPTION, APPLICATION_PROPERTIES.getTitleDescription());
            context.put(AUTHOR, APPLICATION_PROPERTIES.getAuthor());
            context.put(EMAIL, APPLICATION_PROPERTIES.getAuthorEmail());
            context.put(VERSION, APPLICATION_PROPERTIES.getVersion());
            context.put(LAST_UPDATE_DATE, APPLICATION_PROPERTIES.getReleaseDateToString());
            StringWriter stringWriter = new StringWriter();
            template.merge(context, stringWriter);
            aboutProgramHtmlString = stringWriter.toString();
        } catch (Exception e) {
            log.error("Can't load velocity template", e);
        }
    }

    /**
     * Returns about program html string.
     *
     * @return about program html string
     */
    public static String getAboutProgramHtmlString() {
        return aboutProgramHtmlString;
    }
}
