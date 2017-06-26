package eca.jdbc;

/**
 * Implements SQLite datasource descriptor.
 * @author Roman Batygin
 */
public class SQLiteConnectionDescriptor extends MSAccessConnectionDescriptor {

    /**
     * Creates <tt>SQLiteConnectionDescriptor</tt> object with given options.
     * @param host datasource host
     * @param dataBaseName database name
     * @param login user login
     * @param password user password
     */
    public SQLiteConnectionDescriptor(String host, String dataBaseName, String login, String password) {
        super(host, dataBaseName, login, password);
    }

    /**
     * Creates <tt>SQLiteConnectionDescriptor</tt> object with default options.
     */
    public SQLiteConnectionDescriptor() {

    }

    @Override
    public String getProtocol() {
        return "jdbc:sqlite:";
    }
}
