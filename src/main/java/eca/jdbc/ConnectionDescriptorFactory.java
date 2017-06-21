package eca.jdbc;

/**
 * @author Roman Batygin
 */

public class ConnectionDescriptorFactory {

    public static final String MYSQL = "MySQL";

    public static final String ORACLE = "Oracle";

    public static final String POSTGRESQL = "PostgreSQL";

    public static final String MSSQL = "SQL Server";

    public static final String MS_ACCESS = "MS Access";

    public static final String SQLite = "SQLite";

    private static MySQLConnectionDescriptor MY_SQL_CONNECTION_DESCRIPTOR;

    private static OracleConnectionDescriptor ORACLE_CONNECTION_DESCRIPTOR;

    private static PostgreSQLConnectionDescriptor POSTGRESQL_CONNECTION_DESCRIPTOR;

    private static MSSQLConnectionDescriptor MSSQL_CONNECTION_DESCRIPTOR;

    private static SQLiteConnectionDescriptor SQLITE_CONNECTION_DESCRIPTOR;

    private static MSAccessConnectionDescriptor MSACCESS_CONNECTION_DESCRIPTOR;

    private ConnectionDescriptorFactory() {

    }

    public static MySQLConnectionDescriptor getMySqlConnectionDescriptor() {
        if (MY_SQL_CONNECTION_DESCRIPTOR == null) {
            MY_SQL_CONNECTION_DESCRIPTOR = new MySQLConnectionDescriptor("localhost",3306,"","",
                    "");
        }
        return MY_SQL_CONNECTION_DESCRIPTOR;
    }

    public static OracleConnectionDescriptor getOracleConnectionDescriptor() {
        if (ORACLE_CONNECTION_DESCRIPTOR == null) {
            ORACLE_CONNECTION_DESCRIPTOR = new OracleConnectionDescriptor("localhost",1521,"XE","","");
        }
        return ORACLE_CONNECTION_DESCRIPTOR;
    }

    public static PostgreSQLConnectionDescriptor getPostgreSQLConnectionDescriptor() {
        if (POSTGRESQL_CONNECTION_DESCRIPTOR == null) {
            POSTGRESQL_CONNECTION_DESCRIPTOR = new PostgreSQLConnectionDescriptor("localhost",5432,"","","");
        }
        return POSTGRESQL_CONNECTION_DESCRIPTOR;
    }

    public static MSSQLConnectionDescriptor getMSSqlConnectionDescriptor() {
        if (MSSQL_CONNECTION_DESCRIPTOR == null) {
            MSSQL_CONNECTION_DESCRIPTOR = new MSSQLConnectionDescriptor("localhost",1433,"","","");
        }
        return MSSQL_CONNECTION_DESCRIPTOR;
    }

    public static MSAccessConnectionDescriptor getMSAccessConnectionDescriptor() {
        if (MSACCESS_CONNECTION_DESCRIPTOR == null) {
            MSACCESS_CONNECTION_DESCRIPTOR = new MSAccessConnectionDescriptor("D:/","db.accdb","","");
        }
        return MSACCESS_CONNECTION_DESCRIPTOR;
    }

    public static SQLiteConnectionDescriptor getSqliteConnectionDescriptor() {
        if (SQLITE_CONNECTION_DESCRIPTOR == null) {
            SQLITE_CONNECTION_DESCRIPTOR = new SQLiteConnectionDescriptor("c:/","","","");
        }
        return SQLITE_CONNECTION_DESCRIPTOR;
    }

    public static ConnectionDescriptor getConnectionDescriptor(String name) {
        ConnectionDescriptor connectionDescriptor = null;
        switch (name) {
            case MYSQL : {
                connectionDescriptor = getMySqlConnectionDescriptor();
                break;
            }
            case ORACLE : {
                connectionDescriptor = getOracleConnectionDescriptor();
                break;
            }
            case POSTGRESQL : {
                connectionDescriptor = getPostgreSQLConnectionDescriptor();
                break;
            }
            case MSSQL : {
                connectionDescriptor = getMSSqlConnectionDescriptor();
                break;
            }
            case MS_ACCESS : {
                connectionDescriptor = getMSAccessConnectionDescriptor();
                break;
            }
            case SQLite : {
                connectionDescriptor = getSqliteConnectionDescriptor();
                break;
            }
        }
        return connectionDescriptor;
    }

    public static ConnectionDescriptor createConnectionDescriptor(String name) {
        ConnectionDescriptor connectionDescriptor = null;
        switch (name) {
            case MYSQL : {
                connectionDescriptor = new MySQLConnectionDescriptor();
                break;
            }
            case ORACLE : {
                connectionDescriptor = new OracleConnectionDescriptor();
                break;
            }
            case POSTGRESQL : {
                connectionDescriptor = new PostgreSQLConnectionDescriptor();
                break;
            }
            case MSSQL : {
                connectionDescriptor = new MSSQLConnectionDescriptor();
                break;
            }
            case MS_ACCESS : {
                connectionDescriptor = new MSAccessConnectionDescriptor();
                break;
            }
            case SQLite : {
                connectionDescriptor = new SQLiteConnectionDescriptor();
                break;
            }
        }
        return connectionDescriptor;
    }
}
