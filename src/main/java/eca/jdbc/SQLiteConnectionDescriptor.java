package eca.jdbc;

/**
 * @author Roman Batygin
 */

public class SQLiteConnectionDescriptor extends MSAccessConnectionDescriptor {

    public SQLiteConnectionDescriptor(String host, String dataBaseName, String login, String password) {
        super(host, dataBaseName, login, password);
    }

    public SQLiteConnectionDescriptor() {

    }

    @Override
    public String getProtocol() {
        return "jdbc:sqlite:";
    }
}
