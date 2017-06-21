package eca.jdbc;

import net.ucanaccess.jdbc.UcanaccessDriver;

/**
 * @author Roman Batygin
 */

public class MSAccessConnectionDescriptor extends ConnectionDescriptor {

    public MSAccessConnectionDescriptor(String host, String dataBaseName, String login, String password) {
        super(host, 0, dataBaseName, login, password);
    }

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
