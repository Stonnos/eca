package eca.db;

/**
 * Implements PostreSQL  datasource descriptor.
 *
 * @author Roman Batygin
 */
public class PostgreSQLConnectionDescriptor extends MySQLConnectionDescriptor {

    /**
     * Creates <tt>PostgreSQLConnectionDescriptor</tt> object with given options.
     *
     * @param host         datasource host
     * @param port         datasource port number
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    public PostgreSQLConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(host, port, dataBaseName, login, password);
    }

    /**
     * Creates <tt>PostgreSQLConnectionDescriptor</tt> object with default options.
     */
    public PostgreSQLConnectionDescriptor() {
    }

    @Override
    public String getProtocol() {
        return "jdbc:postgresql://";
    }
}
