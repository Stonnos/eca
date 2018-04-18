package eca.data.db;

import eca.data.db.ConnectionDescriptor;
import eca.data.db.DataBaseType;

/**
 * Implements PostreSQL  datasource descriptor.
 *
 * @author Roman Batygin
 */
public class PostgreSQLConnectionDescriptor extends ConnectionDescriptor {

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
        super(DataBaseType.POSTGRESQL, host, port, dataBaseName, login, password);
    }

    /**
     * Creates <tt>PostgreSQLConnectionDescriptor</tt> object with default options.
     */
    public PostgreSQLConnectionDescriptor() {
        super(DataBaseType.POSTGRESQL);
    }

    @Override
    public String getUrl() {
        return getProtocol() + getHost() + ":" + getPort() + "/" + getDataBaseName();
    }

    @Override
    public String getProtocol() {
        return "jdbc:postgresql://";
    }
}
