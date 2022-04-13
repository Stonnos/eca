package eca.data.db;

import eca.data.AbstractDataLoader;
import eca.data.db.model.InstancesResultSet;
import weka.core.Instances;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import static eca.util.Utils.commaSeparatorSplit;

/**
 * Implements loading data from database.
 *
 * @author Roman Batygin
 */
public class JdbcQueryExecutor extends AbstractDataLoader<String> implements AutoCloseable {

    /**
     * Database connection object
     **/
    private Connection connection;

    /**
     * Datasource descriptor
     **/
    private ConnectionDescriptor connectionDescriptor;

    private final InstancesExtractor instancesExtractor = new InstancesExtractor();

    private final InstancesResultSetConverter instancesResultSetConverter = new InstancesResultSetConverter();

    /**
     * Default constructor.
     */
    public JdbcQueryExecutor() {
        this.instancesResultSetConverter.setDateFormat(getDateFormat());
    }

    /**
     * Opens connection with database.
     *
     * @throws SQLException if a database access error occurs
     */
    public void open() throws SQLException, ClassNotFoundException {
        Class.forName(connectionDescriptor.getDriver());
        connection = DriverManager.getConnection(connectionDescriptor.getUrl(),
                connectionDescriptor.getLogin(), connectionDescriptor.getPassword());
    }


    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Retrieves list of all of this database's SQL keywords that are NOT also SQL:2003 keywords.
     *
     * @return the list of this database's keywords that are not also SQL:2003 keywords
     * @throws SQLException if a database access error occurs
     */
    public String[] getSqlKeywords() throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        return commaSeparatorSplit(databaseMetaData.getSQLKeywords());
    }

    /**
     * Returns <tt>ConnectionDescriptor</tt> object.
     *
     * @return <tt>ConnectionDescriptor</tt> object
     */
    public ConnectionDescriptor getConnectionDescriptor() {
        return connectionDescriptor;
    }

    /**
     * Sets <tt>ConnectionDescriptor</tt> object.
     *
     * @param connectionDescriptor <tt>ConnectionDescriptor</tt> object
     */
    public void setConnectionDescriptor(ConnectionDescriptor connectionDescriptor) {
        Objects.requireNonNull(connectionDescriptor, "Connection descriptor is not specified!");
        Objects.requireNonNull(connectionDescriptor.getDriver(), "Driver is not specified!");
        this.connectionDescriptor = connectionDescriptor;
    }

    @Override
    public Instances loadInstances() throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(getSource())) {
            InstancesResultSet instancesResultSet = instancesExtractor.extractData(resultSet);
            Instances instances = instancesResultSetConverter.convert(instancesResultSet);
            instances.setClassIndex(instances.numAttributes() - 1);
            return instances;
        }
    }
}
