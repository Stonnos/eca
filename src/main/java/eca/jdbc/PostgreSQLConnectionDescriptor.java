package eca.jdbc;

/**
 * Implements PostreSQL  datasource descriptor.
 * @author Roman Batygin
 */
public class PostgreSQLConnectionDescriptor extends MySQLConnectionDescriptor {

    /**
     * Creates <tt>PostgreSQLConnectionDescriptor</tt> object with given parameters.
     * @param host datasource host
     * @param port datasource port number
     * @param dataBaseName database name
     * @param login user login
     * @param password user password
     */
    public PostgreSQLConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(host, port, dataBaseName, login, password);
    }

    /**
     * Creates <tt>PostgreSQLConnectionDescriptor</tt> object with default parameters.
     */
    public PostgreSQLConnectionDescriptor() {
    }

    @Override
    public String getProtocol() {
        return "jdbc:postgresql://";
    }
}
