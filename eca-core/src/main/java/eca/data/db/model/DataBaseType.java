package eca.data.db.model;

import eca.core.DescriptiveEnum;

/**
 * Data base type enum.
 *
 * @author Roman Batygin
 */

public enum DataBaseType implements DescriptiveEnum {

    /**
     * MySQL data base.
     */
    MYSQL(DataBaseDictionaryTypes.MYSQL, false) {
        @Override
        public <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor) {
            return dataBaseTypeVisitor.caseMySql();
        }
    },

    /**
     * PostgreSQL data base.
     */
    POSTGRESQL(DataBaseDictionaryTypes.POSTGRESQL, false) {
        @Override
        public <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor) {
            return dataBaseTypeVisitor.casePostgreSQL();
        }
    },

    /**
     * Microsoft Access data base.
     */
    MS_ACCESS(DataBaseDictionaryTypes.MS_ACCESS, true) {
        @Override
        public <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor) {
            return dataBaseTypeVisitor.caseMSAccess();
        }
    },

    /**
     * SQL server data base.
     */
    MSSQL(DataBaseDictionaryTypes.MSSQL, false) {
        @Override
        public <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor) {
            return dataBaseTypeVisitor.caseMSSQL();
        }
    },

    /**
     * SQLite data base.
     */
    SQLITE(DataBaseDictionaryTypes.SQLITE, true) {
        @Override
        public <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor) {
            return dataBaseTypeVisitor.caseSQLite();
        }
    };

    private String description;
    private boolean embedded;

    /**
     * Database type constructor.
     *
     * @param description - database description
     * @param embedded    - is database embedded?
     */
    DataBaseType(String description, boolean embedded) {
        this.description = description;
        this.embedded = embedded;
    }

    /**
     * Returns data base description.
     *
     * @return data base description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Return <tt>true</tt> if database is embedded.
     *
     * @return <tt>true</tt> if database is embedded
     */
    public boolean isEmbedded() {
        return embedded;
    }

    /**
     * Visitor pattern common method
     *
     * @param dataBaseTypeVisitor visitor class
     * @param <T>                 generic class
     * @return generic class
     */
    public abstract <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor);
}
