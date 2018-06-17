/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.data.db.JdbcQueryExecutor;

/**
 * @author Roman Batygin
 */
public class DataBaseConnectionAction implements CallbackAction {

    private final JdbcQueryExecutor connection;

    public DataBaseConnectionAction(JdbcQueryExecutor connection) {
        this.connection = connection;
    }

    @Override
    public void apply() throws Exception {
        connection.open();
    }
}
