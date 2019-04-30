package eca.gui.service;

import eca.config.ConfigurationService;
import eca.config.ProjectInfo;
import eca.config.VelocityConfigService;
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

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String TEMPLATE_FILE_NAME = "vm-templates/aboutProgramTemplate.vm";

    private static String aboutProgramHtmlString;

    private AboutProgramService() {
    }

    static {
        try {
            Template template = VelocityConfigService.getTemplate(TEMPLATE_FILE_NAME);
            VelocityContext context = new VelocityContext();
            ProjectInfo projectInfo = CONFIG_SERVICE.getApplicationConfig().getProjectInfo();
            context.put(TITLE, projectInfo.getTitle());
            context.put(DESCRIPTION, projectInfo.getTitleDescription());
            context.put(AUTHOR, projectInfo.getAuthor());
            context.put(EMAIL, projectInfo.getAuthorEmail());
            context.put(VERSION, projectInfo.getVersion());
            context.put(LAST_UPDATE_DATE, projectInfo.getReleaseDate());
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
