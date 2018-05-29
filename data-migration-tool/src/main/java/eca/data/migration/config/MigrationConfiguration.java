package eca.data.migration.config;

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
}
