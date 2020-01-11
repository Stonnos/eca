package eca.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * Velocity utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class VelocityUtils {

    private static final String CP1251 = "cp1251";

    /**
     * Merges context and then writes html result string to file.
     *
     * @param file     - target file
     * @param template - velocity template
     * @param context  - velocity context
     * @throws IOException in case of an I/O error
     */
    public static void mergeAndWrite(File file, Template template, VelocityContext context) throws IOException {
        String htmlString = mergeContext(template, context);
        FileUtils.write(file, htmlString, Charset.forName(CP1251));
    }

    /**
     * Merges velocity context.
     *
     * @param template - velocity template
     * @param context  - velocity context
     * @return html result string
     */
    public static String mergeContext(Template template, VelocityContext context) {
        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);
        return stringWriter.toString();
    }
}
