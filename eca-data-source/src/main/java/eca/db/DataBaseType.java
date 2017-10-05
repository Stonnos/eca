package eca.db;

/**
 * Data base type enum.
 * @author Roman Batygin
 */

public enum DataBaseType {

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
     * Oracle data base.
     */
    ORACLE(DataBaseDictionaryTypes.ORACLE, false) {
        @Override
        public <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor) {
            return dataBaseTypeVisitor.caseOracle();
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

    DataBaseType(String description, boolean embedded) {
        this.description = description;
        this.embedded = embedded;
    }

    /**
     * Returns data base description.
     *
     * @return data base description
     */
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
     * Returns data bases description.
     * @return data bases description
     */
    public static String[] getDescriptions() {
        DataBaseType[] values = values();
        String[] descriptions = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            descriptions[i] = values[i].getDescription();
        }
        return descriptions;
    }

    /**
     * Finds distance function type by description
     *
     * @param description description string.
     * @return {@link DataBaseType} object
     */
    public static DataBaseType findByDescription(String description) {
        for (DataBaseType dataBaseType : values()) {
            if (dataBaseType.getDescription().equals(description)) {
                return dataBaseType;
            }
        }
        return null;
    }

    /**
     * Visitor pattern common method
     *
     * @param dataBaseTypeVisitor visitor class
     * @param <T> generic class
     * @return generic class
     */
    public abstract <T> T handle(DataBaseTypeVisitor<T> dataBaseTypeVisitor);
}
