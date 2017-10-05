package eca.db;

/**
 * Implements Microsoft SQL Server datasource descriptor.
 *
 * @author Roman Batygin
 */
public class MSSQLConnectionDescriptor extends ConnectionDescriptor {

    /**
     * Creates <tt>MSSQLConnectionDescriptor</tt> object with given options.
     *
     * @param host         datasource host
     * @param port         datasource port number
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    public MSSQLConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(DataBaseType.MSSQL, host, port, dataBaseName, login, password);
    }

    /**
     * Creates <tt>MSSQLConnectionDescriptor</tt> object with default options.
     */
    public MSSQLConnectionDescriptor() {
        super(DataBaseType.MSSQL);
    }

    @Override
    public String getUrl() {
        return getProtocol() + getHost() + ":" + getPort() + ";databaseName=" + getDataBaseName();
    }

    @Override
    public String getProtocol() {
        return "jdbc:sqlserver://";
    }
}
