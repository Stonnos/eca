package eca.data.migration.config;

import eca.data.db.SqlQueryHelper;
import eca.data.db.SqlTypeUtils;
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
     * Creates sql query helper bean.
     *
     * @return sql query helper bean
     */
    @Bean
    public SqlQueryHelper sqlQueryHelper() {
        SqlQueryHelper sqlQueryHelper = new SqlQueryHelper();
        sqlQueryHelper.setDateColumnType(SqlTypeUtils.TIMESTAMP_TYPE);
        return sqlQueryHelper;
    }
}
