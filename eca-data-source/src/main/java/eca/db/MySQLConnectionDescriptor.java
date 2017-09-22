/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.db;

/**
 * Implements MySQL datasource descriptor.
 *
 * @author Roman Batygin
 */
public class MySQLConnectionDescriptor extends ConnectionDescriptor {

    /**
     * Creates <tt>MySQLConnectionDescriptor</tt> object with given options.
     *
     * @param host         datasource host
     * @param port         datasource port number
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    public MySQLConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(host, port, dataBaseName, login, password);
    }

    /**
     * Creates <tt>MySQLConnectionDescriptor</tt> object with default options.
     */
    public MySQLConnectionDescriptor() {

    }

    @Override
    public String getUrl() {
        return getProtocol() + getHost() + ":" + getPort() + "/" + getDataBaseName();
    }

    @Override
    public String getProtocol() {
        return "jdbc:mysql://";
    }
}
