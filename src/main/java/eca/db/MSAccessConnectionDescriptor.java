package eca.db;

import net.ucanaccess.jdbc.UcanaccessDriver;

/**
 * Implements Microsoft Access datasource descriptor.
 * @author Roman Batygin
 */
public class MSAccessConnectionDescriptor extends ConnectionDescriptor {

    /**
     * Creates <tt>MSAccessConnectionDescriptor</tt> object with given options.
     * @param host datasource host
     * @param dataBaseName database name
     * @param login user login
     * @param password user password
     */
    public MSAccessConnectionDescriptor(String host, String dataBaseName, String login, String password) {
        super(host, 0, dataBaseName, login, password);
    }

    /**
     * Creates <tt>MSAccessConnectionDescriptor</tt> object with default options.
     */
    public MSAccessConnectionDescriptor() {

    }

    @Override
    public boolean isEmbedded() {
        return true;
    }

    @Override
    public String getUrl() {
        return getProtocol() + getHost() + getDataBaseName();
    }

    @Override
    public String getProtocol() {
        return UcanaccessDriver.URL_PREFIX;
    }
}
