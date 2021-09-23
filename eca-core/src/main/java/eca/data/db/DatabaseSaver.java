package eca.data.db;

import eca.data.db.model.DataBaseTypeVisitor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instance;
import weka.core.Instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import static eca.data.db.SqlTypeUtils.REAL_COLUMN_TYPE;
import static eca.data.db.SqlTypeUtils.TEXT_COLUMN_TYPE;
import static eca.data.db.SqlTypeUtils.TIMESTAMP_TYPE;

/**
 * Implements instances saving into database.
 *
 * @author Roman Batygin
 */
@Slf4j
public class DatabaseSaver {

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

    /**
     * Saves data into database.
     *
     * @param data - instances object
     * @throws Exception in case of errors
     */
    public void write(Instances data) throws Exception {
        log.info("Staring to save data into table '{}'", tableName);
        Class.forName(connectionDescriptor.getDriver());
        Connection connection = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(connectionDescriptor.getUrl(), connectionDescriptor.getLogin(),
                    connectionDescriptor.getPassword());
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            log.info("Starting to create new table '{}'", tableName);
            statement.execute(sqlQueryHelper.buildCreateTableQuery(tableName, data));
            log.info("Table '{}' has been created", tableName);
            log.info("Starting to insert rows into table '{}'", tableName);
            String insertQuery = sqlQueryHelper.buildPreparedInsertQuery(tableName, data);
            preparedStatement = connection.prepareStatement(insertQuery);
            saveInstances(data, preparedStatement);
            connection.commit();
            log.info("Data has been saved into table '{}'", tableName);
        } catch (Exception ex) {
            rollback(connection);
            throw new Exception(ex.getMessage());
        } finally {
            closeStatement(statement);
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private void saveInstances(Instances data, PreparedStatement preparedStatement) throws SQLException {
        for (Instance instance : data) {
            prepareInstance(instance, preparedStatement);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }

    private void prepareInstance(Instance instance, PreparedStatement preparedStatement) throws SQLException {
        Object[] preparedValues = sqlQueryHelper.prepareQueryParameters(instance);
        for (int i = 0; i < preparedValues.length; i++) {
            preparedStatement.setObject(i + 1, preparedValues[i]);
        }
    }

    private void closeStatement(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    private void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private void rollback(Connection connection) throws SQLException {
        if (connection != null) {
            connection.rollback();
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
                sqlQueryHelper.setDateColumnType(TIMESTAMP_TYPE);
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
                sqlQueryHelper.setNumericColumnType(REAL_COLUMN_TYPE);
                sqlQueryHelper.setDateColumnType(TEXT_COLUMN_TYPE);
                sqlQueryHelper.setVarcharColumnType(TEXT_COLUMN_TYPE);
                sqlQueryHelper.setUseDateInStringFormat(true);
                return null;
            }
        });
    }
}
