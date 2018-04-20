package eca.config;

import lombok.Data;

/**
 * Application config.
 *
 * @author Roman Batygin
 */
@Data
public class ApplicationConfig {

    /**
     * Project info
     */
    private ProjectInfo projectInfo;
    /**
     * Max. fraction digits
     */
    private Integer fractionDigits;
    /**
     * Max. fraction digits supported by application
     */
    private Integer minFractionDigits;
    /**
     * Min. fraction digits supported by application
     */
    private Integer maxFractionDigits;
    /**
     * Max. training data list size
     */
    private Integer maxDataListSize;
    /**
     * Eca icon path
     */
    private String iconUrl;
    /**
     * Eca logotype path
     */
    private String logotypeUrl;
    /**
     * Dismiss time for tooltips in milliseconds
     */
    private Integer tooltipDismissTime;
    /**
     * Max threads
     */
    private Integer maxThreads;
    /**
     * AUC threshold value for significant attributes selection.
     */
    private Double aucThresholdValue;
    /**
     * Date format for date attributes
     */
    private String dateFormat;
    /**
     * Cross - validation config
     */
    private CrossValidationConfig crossValidationConfig;
    /**
     * Experiment config
     */
    private ExperimentConfig experimentConfig;

}
