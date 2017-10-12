/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.db;

/**
 * Implements Oracle connection descriptor.
 *
 * @author Roman Batygin
 */
public class OracleConnectionDescriptor extends ConnectionDescriptor {

    /**
     * Creates <tt>OracleConnectionDescriptor</tt> object with given options.
     *
     * @param host         datasource host
     * @param port         datasource port number
     * @param dataBaseName database name
     * @param login        user login
     * @param password     user password
     */
    public OracleConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(DataBaseType.ORACLE, host, port, dataBaseName, login, password);
    }

    /**
     * Creates <tt>OracleConnectionDescriptor</tt> object with default options.
     */
    public OracleConnectionDescriptor() {
        super(DataBaseType.ORACLE);
    }

    @Override
    public String getUrl() {
        return getProtocol() + getHost() + ":" + getPort() + ":" + getDataBaseName();
    }

    @Override
    public String getProtocol() {
        return "jdbc:oracle:thin:@";
    }
}
