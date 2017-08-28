/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.db;

import org.apache.commons.lang3.StringUtils;

/**
 * Datasource descriptor.
 *
 * @author Roman93
 */
public abstract class ConnectionDescriptor implements java.io.Serializable {

    private String host = StringUtils.EMPTY;
    private int port;
    private String dataBaseName = StringUtils.EMPTY;
    private String login = StringUtils.EMPTY;
    private String password = StringUtils.EMPTY;

    /**
     * Creates <tt>ConnectionDescriptor</tt> object with default options.
     */
    protected ConnectionDescriptor() {
    }

    /**
     * Creates <tt>ConnectionDescriptor</tt> object with given options.
     *
     * @param host         datasource host
     * @param port         datasource port number
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    protected ConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        this.host = host;
        this.port = port;
        this.dataBaseName = dataBaseName;
        this.login = login;
        this.password = password;
    }

    /**
     * Return <tt>true</tt> if database is embedded.
     *
     * @return <tt>true</tt> if database is embedded
     */
    public boolean isEmbedded() {
        return false;
    }

    /**
     * Returns datasource host.
     *
     * @return datasource host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets datasource host.
     *
     * @param host datasource host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Returns datasource port number.
     *
     * @return datasource port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets datasource port number.
     *
     * @param port datasource port number
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns user login.
     *
     * @return user login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets  user login.
     *
     * @param login user login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Returns user password.
     *
     * @return user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets user password.
     *
     * @param password user password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns database name.
     *
     * @return database name
     */
    public String getDataBaseName() {
        return dataBaseName;
    }

    /**
     * Sets database name.
     *
     * @param dataBaseName database name
     */
    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    /**
     * Returns url string of datasource.
     *
     * @return url string of datasource
     */
    public abstract String getUrl();

    /**
     * Returns connection protocol.
     *
     * @return connection protocol
     */
    public abstract String getProtocol();

}
