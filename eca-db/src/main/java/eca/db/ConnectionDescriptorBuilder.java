package eca.db;

/**
 * Class for creation {@link ConnectionDescriptor} objects.
 * @author Roman Batygin
 */
public class ConnectionDescriptorBuilder implements DataBaseTypeVisitor<ConnectionDescriptor> {

    private static final DataBaseProperties DATA_BASE_PROPERTIES = DataBaseProperties.getInstance();

    @Override
    public ConnectionDescriptor caseMySql() {
        MySQLConnectionDescriptor mySQLConnectionDescriptor = new MySQLConnectionDescriptor();
        mySQLConnectionDescriptor.setDriver(DATA_BASE_PROPERTIES.getMysqlDriverProperty());
        return mySQLConnectionDescriptor;
    }

    @Override
    public ConnectionDescriptor casePostgreSQL() {
        PostgreSQLConnectionDescriptor postgreSQLConnectionDescriptor = new PostgreSQLConnectionDescriptor();
        postgreSQLConnectionDescriptor.setDriver(DATA_BASE_PROPERTIES.getPostgresDriverProperty());
        return postgreSQLConnectionDescriptor;
    }

    @Override
    public ConnectionDescriptor caseOracle() {
        OracleConnectionDescriptor oracleConnectionDescriptor = new OracleConnectionDescriptor();
        oracleConnectionDescriptor.setDriver(DATA_BASE_PROPERTIES.getOracleDriverProperty());
        return oracleConnectionDescriptor;
    }

    @Override
    public ConnectionDescriptor caseMSAccess() {
        MSAccessConnectionDescriptor msAccessConnectionDescriptor = new MSAccessConnectionDescriptor();
        msAccessConnectionDescriptor.setDriver(DATA_BASE_PROPERTIES.getMsAccessDriverProperty());
        return msAccessConnectionDescriptor;
    }

    @Override
    public ConnectionDescriptor caseMSSQL() {
        MSSQLConnectionDescriptor mssqlConnectionDescriptor = new MSSQLConnectionDescriptor();
        mssqlConnectionDescriptor.setDriver(DATA_BASE_PROPERTIES.getMssqlDriverProperty());
        return mssqlConnectionDescriptor;
    }

    @Override
    public ConnectionDescriptor caseSQLite() {
        SQLiteConnectionDescriptor sqLiteConnectionDescriptor = new SQLiteConnectionDescriptor();
        sqLiteConnectionDescriptor.setDriver(DATA_BASE_PROPERTIES.getSqliteDriverProperty());
        return sqLiteConnectionDescriptor;
    }
}
