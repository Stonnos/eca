package eca.data.db;

import eca.data.db.model.DataBaseTypeVisitor;

/**
 * Class for creation {@link ConnectionDescriptor} objects.
 *
 * @author Roman Batygin
 */
public class ConnectionDescriptorBuilder implements DataBaseTypeVisitor<ConnectionDescriptor> {

    @Override
    public ConnectionDescriptor caseMySql() {
        return new MySQLConnectionDescriptor();
    }

    @Override
    public ConnectionDescriptor casePostgreSQL() {
        return new PostgreSQLConnectionDescriptor();
    }

    @Override
    public ConnectionDescriptor caseMSAccess() {
        return new MSAccessConnectionDescriptor();
    }

    @Override
    public ConnectionDescriptor caseMSSQL() {
        return new MSSQLConnectionDescriptor();
    }

    @Override
    public ConnectionDescriptor caseSQLite() {
        return new SQLiteConnectionDescriptor();
    }
}
