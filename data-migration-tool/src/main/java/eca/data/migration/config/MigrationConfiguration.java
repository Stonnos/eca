package eca.data.migration.config;

import eca.data.file.FileDataLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Migration tool configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableScheduling
public class MigrationConfiguration {

    /**
     * Creates migration config bean.
     *
     * @return migration config bean
     */
    @Bean
    public MigrationConfig migrationConfig() {
        return new MigrationConfig();
    }

    /**
     * Creates data loader bean.
     *
     * @param migrationConfig - migration config bean
     * @return data loader bean
     */
    @Bean
    public FileDataLoader dataLoader(MigrationConfig migrationConfig) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(migrationConfig.getDateFormat());
        return dataLoader;
    }
}
