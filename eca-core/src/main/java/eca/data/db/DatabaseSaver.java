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
     * TO_DATE function format for Oracle database
     */
    private static final String TO_DATE_FUNCTION_FORMAT = "TO_DATE('%s', 'yyyy-mm-dd hh24:mi:ss')";

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
     * Sql query helper
     */
    private SqlQueryHelper sqlQueryHelper;

    /**
     * Constructor with params.
     *
     * @param connectionDescriptor - connection descriptor
     */
    public DatabaseSaver(ConnectionDescriptor connectionDescriptor) {
        Objects.requireNonNull(connectionDescriptor, "Connection descriptor must be specified!");
        Objects.requireNonNull(connectionDescriptor.getDriver(), "Driver is not specified!");
        this.connectionDescriptor = connectionDescriptor;
        this.initSqlQueryHelper();
    }

    @Override
    public void write(Instances data) throws Exception {
        log.info("Staring to save data into table '{}'", tableName);
        Class.forName(connectionDescriptor.getDriver());
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(connectionDescriptor.getUrl(), connectionDescriptor.getLogin(),
                    connectionDescriptor.getPassword());
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            log.info("Starting to create new table '{}'", tableName);
            statement.execute(sqlQueryHelper.buildCreateTableQuery(tableName, data));
            log.info("Table '{}' has been created", tableName);
            log.info("Starting to insert rows into table '{}'", tableName);
            for (Instance instance : data) {
                statement.execute(sqlQueryHelper.buildInsertQuery(tableName, data, instance));
            }
            connection.commit();
            log.info("Data has been saved into table '{}'", tableName);
        } catch (Exception ex) {
            if (connection != null) {
                connection.rollback();
            }
            throw new Exception(ex.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private void initSqlQueryHelper() {
        sqlQueryHelper = new SqlQueryHelper();
        connectionDescriptor.getDataBaseType().handle(new DataBaseTypeVisitor<Void>() {
            @Override
            public Void caseMySql() {
                return null;
            }

            @Override
            public Void casePostgreSQL() {
                sqlQueryHelper.setDateColumnType(SqlTypeUtils.TIMESTAMP_TYPE);
                return null;
            }

            @Override
            public Void caseOracle() {
                sqlQueryHelper.setDateColumnType(SqlTypeUtils.DATE_TYPE);
                sqlQueryHelper.setDateValueFormat(TO_DATE_FUNCTION_FORMAT);
                return null;
            }

            @Override
            public Void caseMSAccess() {
                return null;
            }

            @Override
            public Void caseMSSQL() {
                return null;
            }

            @Override
            public Void caseSQLite() {
                return null;
            }
        });
    }
}
