package eca.config;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * Application properties config.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ApplicationProperties {

    private static final String PROPERTIES_FILE = "application.properties";
    private static final String ECA_VERSION_PROPERTY = "eca.version";
    private static final String ECA_RELEASE_DATE_PROPERTY = "eca.releaseDate";
    private static final String ECA_DIGITS_PROPERTY = "eca.defaultDigits";
    private static final String ECA_TITLE_PROPERTY = "eca.title";
    private static final String ECA_TITLE_DESCRIPTION_PROPERTY = "eca.titleDescription";
    private static final String ECA_ICON_URL_PROPERTY = "eca.iconUrl";
    private static final String ECA_LOGOTYPE_URL_PROPERTY = "eca.logotypeUrl";
    private static final String ECA_AUTHOR_PROPERTY = "eca.author";
    private static final String ECA_AUTHOR_EMAIL_PROPERTY = "eca.authorEmail";
    private static final String ECA_TOOLTIP_DISMISS_TIME = "eca.tooltipDismissTime";
    private static final String CROSS_VALIDATION_FOLDS = "crossValidation.folds";
    private static final String CROSS_VALIDATION_TESTS = "crossValidation.tests";
    private static final String ECA_MIN_DIGITS_PROPERTY = "eca.minDigits";
    private static final String ECA_MAX_DIGITS_PROPERTY = "eca.maxDigits";
    private static final String ECA_MAX_DATA_LIST_SIZE = "eca.maxDataListSize";
    private static final String EXPERIMENT_NUM_BEST_RESUTLS = "experiment.numBestResults";

    private static Properties PROPERTIES = new Properties();

    private static ApplicationProperties INSTANCE;

    static {
        try (InputStream stream = ApplicationProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            PROPERTIES.load(stream);
        } catch (Exception ex) {
            log.error("Can't load application properties:", ex);
        }
    }

    private ApplicationProperties() {
    }

    /**
     * Returns <tt>ApplicationProperties</tt> instance.
     *
     * @return <tt>ApplicationProperties</tt> instance
     */
    public static ApplicationProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationProperties();
        }
        return INSTANCE;
    }

    /**
     * Returns project version.
     *
     * @return project version
     */
    public String getVersion() {
        return PROPERTIES.getProperty(ECA_VERSION_PROPERTY);
    }

    /**
     * Returns project release date.
     *
     * @return project release date
     */
    public String getReleaseDateToString() {
        return PROPERTIES.getProperty(ECA_RELEASE_DATE_PROPERTY);
    }

    /**
     * Returns default fraction digits.
     *
     * @return default fraction digits
     */
    public Integer getDefaultFractionDigits() {
        return Integer.valueOf(PROPERTIES.getProperty(ECA_DIGITS_PROPERTY));
    }

    /**
     * Returns minimum fraction digits.
     *
     * @return minimum fraction digits
     */
    public Integer getMinimumFractionDigits() {
        return Integer.valueOf(PROPERTIES.getProperty(ECA_MIN_DIGITS_PROPERTY));
    }

    /**
     * Returns maximum fraction digits.
     *
     * @return maximum fraction digits
     */
    public Integer getMaximumFractionDigits() {
        return Integer.valueOf(PROPERTIES.getProperty(ECA_MAX_DIGITS_PROPERTY));
    }

    /**
     * Returns data maximum list size.
     *
     * @return data maximum list size
     */
    public Integer getMaximumListSizeOfData() {
        return Integer.valueOf(PROPERTIES.getProperty(ECA_MAX_DATA_LIST_SIZE));
    }

    /**
     * Returns project title.
     *
     * @return project title
     */
    public String getTitle() {
        return PROPERTIES.getProperty(ECA_TITLE_PROPERTY);
    }

    /**
     * Returns project icon url.
     *
     * @return project icon url
     */
    public String getIconUrl() {
        return PROPERTIES.getProperty(ECA_ICON_URL_PROPERTY);
    }

    /**
     * Returns logotype url.
     *
     * @return logotype url
     */
    public String getLogotypeUrl() {
        return PROPERTIES.getProperty(ECA_LOGOTYPE_URL_PROPERTY);
    }

    /**
     * Returns project author data.
     *
     * @return project author data
     */
    public String getAuthor() {
        return PROPERTIES.getProperty(ECA_AUTHOR_PROPERTY);
    }

    /**
     * Returns author email.
     *
     * @return author email
     */
    public String getAuthorEmail() {
        return PROPERTIES.getProperty(ECA_AUTHOR_EMAIL_PROPERTY);
    }

    /**
     * Returns project description.
     *
     * @return project description
     */
    public String getTitleDescription() {
        return PROPERTIES.getProperty(ECA_TITLE_DESCRIPTION_PROPERTY);
    }

    /**
     * Returns tooltips dismiss time.
     *
     * @return tooltips dismiss time
     */
    public Integer getTooltipDismissTime() {
        return Integer.parseInt(PROPERTIES.getProperty(ECA_TOOLTIP_DISMISS_TIME));
    }

    /**
     * Return the default number of folds value for k * V cross - validation method.
     *
     * @return the default number of folds value for k * V cross - validation method
     */
    public Integer getNumFolds() {
        return Integer.valueOf(PROPERTIES.getProperty(CROSS_VALIDATION_FOLDS));
    }

    /**
     * Returns the default number of tests value for k * V cross - validation method.
     *
     * @return the default number of tests value for k * V cross - validation method
     */
    public Integer getNumTests() {
        return Integer.valueOf(PROPERTIES.getProperty(CROSS_VALIDATION_TESTS));
    }

    /**
     * Returns the number of best experiment models.
     *
     * @return the number of best experiment models
     */
    public Integer getNumBestResults() {
        return Integer.valueOf(PROPERTIES.getProperty(EXPERIMENT_NUM_BEST_RESUTLS));
    }

}
