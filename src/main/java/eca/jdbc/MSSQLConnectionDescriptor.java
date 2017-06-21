package eca.jdbc;

/**
 * @author Roman Batygin
 */

public class MSSQLConnectionDescriptor extends ConnectionDescriptor {

    public MSSQLConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(host, port, dataBaseName, login, password);
    }

    public MSSQLConnectionDescriptor() {

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
