/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.jdbc;

/**
 *
 * @author Roman93
 */
public abstract class ConnectionDescriptor implements java.io.Serializable {

    private String host = "";
    private int port;
    private String dataBaseName = "";
    private String login = "";
    private String password = "";

    public ConnectionDescriptor() {
    }

    public ConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        this.host = host;
        this.port = port;
        this.dataBaseName = dataBaseName;
        this.login = login;
        this.password = password;
    }

    public boolean isEmbedded() {
        return false;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public abstract String getUrl();

    public abstract String getProtocol();

}
