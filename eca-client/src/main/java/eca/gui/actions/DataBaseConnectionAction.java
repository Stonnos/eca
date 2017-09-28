/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.db.DataBaseQueryExecutor;

/**
 * @author Roman Batygin
 */
public class DataBaseConnectionAction implements CallbackAction {

    private final DataBaseQueryExecutor connection;

    public DataBaseConnectionAction(DataBaseQueryExecutor connection) {
        this.connection = connection;
    }

    public DataBaseQueryExecutor getConnection() {
        return connection;
    }

    @Override
    public void apply() throws Exception {
        connection.open();
    }
}
