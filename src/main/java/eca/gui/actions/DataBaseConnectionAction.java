/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.jdbc.DataBaseConnection;
/**
 *
 * @author Roman93
 */
public class DataBaseConnectionAction implements Actionable {
    
    private final DataBaseConnection connection;
    
    public DataBaseConnectionAction(DataBaseConnection connection) {
        this.connection = connection;
    }
    
    public DataBaseConnection getConnection() {
        return connection;
    }
    
    @Override
    public void action() throws Exception {
        connection.open();
    }
}
