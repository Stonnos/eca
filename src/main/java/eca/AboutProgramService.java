package eca;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

/**
 * @author Roman Batygin
 */

public class AboutProgramService {

    private static final String TEMPLATE_FILE_NAME = "aboutProgramTemplate.vm";

    private static String aboutProgramHtmlString;

    static {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();
        Template template = velocityEngine.getTemplate(TEMPLATE_FILE_NAME);
        VelocityContext context = new VelocityContext();
        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);
        aboutProgramHtmlString = stringWriter.toString();
    }

    public static String getAboutProgramHtmlString() {
        return aboutProgramHtmlString;
    }
}
