package eca.data.db;

/**
 * Implements Microsoft Access datasource descriptor.
 *
 * @author Roman Batygin
 */
public class MSAccessConnectionDescriptor extends ConnectionDescriptor {

    /**
     * Creates <tt>MSAccessConnectionDescriptor</tt> object with given options.
     *
     * @param host         datasource host
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    public MSAccessConnectionDescriptor(String host, String dataBaseName, String login, String password) {
        super(DataBaseType.MS_ACCESS, host, 0, dataBaseName, login, password);
    }

    /**
     * Creates <tt>MSAccessConnectionDescriptor</tt> object with default options.
     */
    public MSAccessConnectionDescriptor() {
        super(DataBaseType.MS_ACCESS);
    }

    @Override
    public String getUrl() {
        return getProtocol() + getHost() + getDataBaseName();
    }

    @Override
    public String getProtocol() {
        return "jdbc:ucanaccess://";
    }
}
