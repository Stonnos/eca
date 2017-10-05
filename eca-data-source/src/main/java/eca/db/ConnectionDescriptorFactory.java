package eca.db;

import org.apache.commons.lang3.StringUtils;

/**
 * Implements factory providing singleton connection descriptor objects.
 *
 * @author Roman Batygin
 */
public class ConnectionDescriptorFactory {

    private static MySQLConnectionDescriptor MY_SQL_CONNECTION_DESCRIPTOR;
    private static OracleConnectionDescriptor ORACLE_CONNECTION_DESCRIPTOR;
    private static PostgreSQLConnectionDescriptor POSTGRESQL_CONNECTION_DESCRIPTOR;
    private static MSSQLConnectionDescriptor MSSQL_CONNECTION_DESCRIPTOR;
    private static SQLiteConnectionDescriptor SQLITE_CONNECTION_DESCRIPTOR;
    private static MSAccessConnectionDescriptor MSACCESS_CONNECTION_DESCRIPTOR;

    private ConnectionDescriptorFactory() {
    }

    /**
     * Returns singleton <tt>MySQLConnectionDescriptor</tt> object.
     *
     * @return singleton <tt>MySQLConnectionDescriptor</tt> object
     */
    public static MySQLConnectionDescriptor getMySqlConnectionDescriptor() {
        if (MY_SQL_CONNECTION_DESCRIPTOR == null) {
            MY_SQL_CONNECTION_DESCRIPTOR =
                    new MySQLConnectionDescriptor("localhost", 3306, StringUtils.EMPTY, StringUtils.EMPTY,
                            StringUtils.EMPTY);
        }
        return MY_SQL_CONNECTION_DESCRIPTOR;
    }

    /**
     * Returns singleton <tt>OracleConnectionDescriptor</tt> object.
     *
     * @return singleton <tt>OracleConnectionDescriptor</tt> object
     */
    public static OracleConnectionDescriptor getOracleConnectionDescriptor() {
        if (ORACLE_CONNECTION_DESCRIPTOR == null) {
            ORACLE_CONNECTION_DESCRIPTOR =
                    new OracleConnectionDescriptor("localhost", 1521, "XE",
                            StringUtils.EMPTY, StringUtils.EMPTY);
        }
        return ORACLE_CONNECTION_DESCRIPTOR;
    }

    /**
     * Returns singleton <tt>PostgreSQLConnectionDescriptor</tt> object.
     *
     * @return singleton <tt>PostgreSQLConnectionDescriptor</tt> object
     */
    public static PostgreSQLConnectionDescriptor getPostgreSQLConnectionDescriptor() {
        if (POSTGRESQL_CONNECTION_DESCRIPTOR == null) {
            POSTGRESQL_CONNECTION_DESCRIPTOR =
                    new PostgreSQLConnectionDescriptor("localhost", 5432, StringUtils.EMPTY, StringUtils.EMPTY,
                            StringUtils.EMPTY);
        }
        return POSTGRESQL_CONNECTION_DESCRIPTOR;
    }

    /**
     * Returns singleton <tt>MSSQLConnectionDescriptor</tt> object.
     *
     * @return singleton <tt>MSSQLConnectionDescriptor</tt> object
     */
    public static MSSQLConnectionDescriptor getMSSqlConnectionDescriptor() {
        if (MSSQL_CONNECTION_DESCRIPTOR == null) {
            MSSQL_CONNECTION_DESCRIPTOR =
                    new MSSQLConnectionDescriptor("localhost", 1433, StringUtils.EMPTY, StringUtils.EMPTY,
                            StringUtils.EMPTY);
        }
        return MSSQL_CONNECTION_DESCRIPTOR;
    }

    /**
     * Returns singleton <tt>MSAccessConnectionDescriptor</tt> object.
     *
     * @return singleton <tt>MSAccessConnectionDescriptor</tt> object
     */
    public static MSAccessConnectionDescriptor getMSAccessConnectionDescriptor() {
        if (MSACCESS_CONNECTION_DESCRIPTOR == null) {
            MSACCESS_CONNECTION_DESCRIPTOR =
                    new MSAccessConnectionDescriptor("D:/", "db.accdb",
                            StringUtils.EMPTY, StringUtils.EMPTY);
        }
        return MSACCESS_CONNECTION_DESCRIPTOR;
    }

    /**
     * Returns singleton <tt>SQLiteConnectionDescriptor</tt> object.
     *
     * @return singleton <tt>SQLiteConnectionDescriptor</tt> object
     */
    public static SQLiteConnectionDescriptor getSqliteConnectionDescriptor() {
        if (SQLITE_CONNECTION_DESCRIPTOR == null) {
            SQLITE_CONNECTION_DESCRIPTOR =
                    new SQLiteConnectionDescriptor("c:/", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
        }
        return SQLITE_CONNECTION_DESCRIPTOR;
    }

}
