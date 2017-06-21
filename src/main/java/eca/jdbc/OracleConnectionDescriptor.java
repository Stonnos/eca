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
public class OracleConnectionDescriptor extends ConnectionDescriptor {
    
    public OracleConnectionDescriptor(String host, int port, String dataBaseName, String login, String password) {
        super(host, port, dataBaseName, login, password);
    }
    
    public OracleConnectionDescriptor() {
        
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
