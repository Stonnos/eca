package eca.jdbc;

/**
 * @author Roman Batygin
 */

public class PostgreSQLConnectionDescriptor extends MySQLConnectionDescriptor {

    public PostgreSQLConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(host, port, dataBaseName, login, password);
    }

    public PostgreSQLConnectionDescriptor() {
    }

    @Override
    public String getProtocol() {
        return "jdbc:postgresql://";
    }
}
