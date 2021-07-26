/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.data.db.JdbcQueryExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Roman Batygin
 */
@Slf4j
public class DataBaseConnectionAction implements CallbackAction {

    private final JdbcQueryExecutor connection;

    public DataBaseConnectionAction(JdbcQueryExecutor connection) {
        this.connection = connection;
    }

    @Override
    public void apply() throws Exception {
        log.info("Attempting connect to database [{}]", connection.getConnectionDescriptor().getUrl());
        connection.open();
        log.info("Connected to database [{}]", connection.getConnectionDescriptor().getUrl());
    }
}
