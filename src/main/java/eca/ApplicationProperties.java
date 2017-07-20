package eca;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * @author Roman Batygin
 */

public class ApplicationProperties {

    private static final String PROPERTIES_FILE = "application.properties";

    private static final String ECA_VERSION_PROPERTY = "eca.version";

    private static final String ECA_RELEASE_DATE_PROPERTY = "eca.release.date";

    private static final String ECA_DIGITS_PROPERTY = "eca.digits";

    private static final String ECA_TITLE_PROPERTY = "eca.title";

    private static final String ECA_TITLE_DESCRIPTION_PROPERTY = "eca.title.description";

    private static final String ECA_ICON_URL_PROPERTY = "eca.icon.url";

    private static final String ECA_LOGOTYPE_URL_PROPERTY = "eca.logotype.url";

    private static final String ECA_AUTHOR_PROPERTY = "eca.author";

    private static final String ECA_AUTHOR_EMAIL_PROPERTY = "eca.author.email";

    private static final String ECA_TOOLTIP_DISMISS_TIME = "eca.tooltip.dismiss.time";

    private static final String CROSS_VALIDATION_FOLDS = "cross.validation.folds";

    private static final String CROSS_VALIDATION_TESTS = "cross.validation.tests";

    private static Properties PROPERTIES = new Properties();

    private static ApplicationProperties INSTANCE;

    static {
        try (InputStream stream = ApplicationProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            PROPERTIES.load(stream);
        }
        catch (Exception e) {}
    }

    private ApplicationProperties() {}

    public static ApplicationProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationProperties();
        }
        return INSTANCE;
    }

    public String getVersion() {
        return PROPERTIES.getProperty(ECA_VERSION_PROPERTY);
    }

    public String getReleaseDateToString() {
        return PROPERTIES.getProperty(ECA_RELEASE_DATE_PROPERTY);
    }

    public int getMaximumFractionDigits() {
        return Integer.valueOf(PROPERTIES.getProperty(ECA_DIGITS_PROPERTY));
    }

    public String getTitle() {
        return PROPERTIES.getProperty(ECA_TITLE_PROPERTY);
    }

    public String getIconUrl() {
        return PROPERTIES.getProperty(ECA_ICON_URL_PROPERTY);
    }

    public String getLogotypeUrl() {
        return PROPERTIES.getProperty(ECA_LOGOTYPE_URL_PROPERTY);
    }

    public String getAuthor() {
        return PROPERTIES.getProperty(ECA_AUTHOR_PROPERTY);
    }

    public String getAuthorEmail() {
        return PROPERTIES.getProperty(ECA_AUTHOR_EMAIL_PROPERTY);
    }

    public String getTitleDescription() {
        return PROPERTIES.getProperty(ECA_TITLE_DESCRIPTION_PROPERTY);
    }

    public int getTooltipDismissTime() {
        return Integer.parseInt(PROPERTIES.getProperty(ECA_TOOLTIP_DISMISS_TIME));
    }

    public int getNumFolds() {
        return Integer.valueOf(PROPERTIES.getProperty(CROSS_VALIDATION_FOLDS));
    }

    public int getNumTests() {
        return Integer.valueOf(PROPERTIES.getProperty(CROSS_VALIDATION_TESTS));
    }

}
