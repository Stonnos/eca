package eca.data.db;

import eca.data.db.ConnectionDescriptor;
import eca.data.db.DataBaseType;

/**
 * Implements SQLite datasource descriptor.
 *
 * @author Roman Batygin
 */
public class SQLiteConnectionDescriptor extends ConnectionDescriptor {

    /**
     * Creates <tt>SQLiteConnectionDescriptor</tt> object with given options.
     *
     * @param host         datasource host
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    public SQLiteConnectionDescriptor(String host, String dataBaseName, String login, String password) {
        super(DataBaseType.SQLITE, host, 0, dataBaseName, login, password);
    }

    /**
     * Creates <tt>SQLiteConnectionDescriptor</tt> object with default options.
     */
    public SQLiteConnectionDescriptor() {
        super(DataBaseType.SQLITE);
    }

    @Override
    public String getUrl() {
        return getProtocol() + getHost() + getDataBaseName();
    }

    @Override
    public String getProtocol() {
        return "jdbc:sqlite:";
    }
}
