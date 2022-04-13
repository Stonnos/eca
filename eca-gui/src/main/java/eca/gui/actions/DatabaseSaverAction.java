package eca.gui.actions;

import eca.data.db.DatabaseSaver;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
@Slf4j
public class DatabaseSaverAction implements CallbackAction {

    private DatabaseSaver databaseSaver;
    private Instances instances;

    public DatabaseSaverAction(DatabaseSaver databaseSaver, Instances instances) {
        this.databaseSaver = databaseSaver;
        this.instances = instances;
    }

    @Override
    public void apply() throws Exception {
        log.info("Starting to save instances [{}] to table [{}] in db [{}]", instances.relationName(),
                databaseSaver.getTableName(), databaseSaver.getConnectionDescriptor().getUrl());
        databaseSaver.write(instances);
        log.info("Instances [{}] has been saved to table [{}] in db [{}]", instances.relationName(),
                databaseSaver.getTableName(), databaseSaver.getConnectionDescriptor().getUrl());
    }
}
