package eca.data.migration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Migration config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("migration")
public class MigrationConfig {

    /**
     * Date format for date attributes
     */
    private String dateFormat;

    /**
     * Training data storage path on the disk
     */
    private String dataStoragePath;

    /**
     * Batch size for data migration
     */
    private Integer batchSize;

    /**
     * Is data migration job enabled?
     */
    private Boolean jobEnabled;
}
