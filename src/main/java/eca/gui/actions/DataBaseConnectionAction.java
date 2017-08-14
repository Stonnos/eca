/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.db.DataBaseConnection;

/**
 * @author Roman93
 */
public class DataBaseConnectionAction implements CallbackAction {

    private final DataBaseConnection connection;

    public DataBaseConnectionAction(DataBaseConnection connection) {
        this.connection = connection;
    }

    public DataBaseConnection getConnection() {
        return connection;
    }

    @Override
    public void apply() throws Exception {
        connection.open();
    }
}
