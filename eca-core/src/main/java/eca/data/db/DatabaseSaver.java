package eca.data.db;

import eca.data.DataSaver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instance;
import weka.core.Instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Objects;

/**
 * Implements instances saving into database.
 *
 * @author Roman Batygin
 */
@Slf4j
public class DatabaseSaver implements DataSaver {

    /**
     * Connection descriptor
     */
    @Getter
    private ConnectionDescriptor connectionDescriptor;

    /**
     * Table name for saving instances
     */
    @Getter
    @Setter
    private String tableName;

    /**
     * Constructor with params.
     *
     * @param connectionDescriptor - connection descriptor
     */
    public DatabaseSaver(ConnectionDescriptor connectionDescriptor) {
        Objects.requireNonNull(connectionDescriptor, "Connection descriptor must be specified!");
        Objects.requireNonNull(connectionDescriptor.getDriver(), "Driver is not specified!");
        this.connectionDescriptor = connectionDescriptor;
    }

    @Override
    public void write(Instances data) throws Exception {
        log.info("Staring to save data into table '{}'", tableName);
        Class.forName(connectionDescriptor.getDriver());
        try (Connection connection = DriverManager.getConnection(connectionDescriptor.getUrl(),
                connectionDescriptor.getLogin(), connectionDescriptor.getPassword());
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            log.info("Starting to create new table '{}'", tableName);
            statement.execute(SqlHelper.buildCreateTableQuery(tableName, data));
            log.info("Table '{}' has been created", tableName);
            log.info("Starting to insert rows into table '{}'", tableName);
            for (Instance instance : data) {
                statement.execute(SqlHelper.buildInsertQuery(tableName, data, instance));
            }
            connection.commit();
            log.info("Data has been saved into table '{}'", tableName);
        }
    }
}
