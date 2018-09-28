package eca.gui.actions;

import eca.data.db.DatabaseSaver;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class DatabaseSaverAction implements CallbackAction {

    private DatabaseSaver databaseSaver;
    private Instances instances;

    public DatabaseSaverAction(DatabaseSaver databaseSaver, Instances instances) {
        this.databaseSaver = databaseSaver;
        this.instances = instances;
    }

    @Override
    public void apply() throws Exception {
        databaseSaver.write(instances);
    }
}
