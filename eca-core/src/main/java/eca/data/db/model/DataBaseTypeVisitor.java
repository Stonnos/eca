package eca.data.db.model;

/**
 * Data base type visitor pattern.
 * @param <T> - generic type
 *
 * @author Roman Batygin
 */
public interface DataBaseTypeVisitor<T> {

    /**
     * Method executed in case if data base type is MYSQL.
     *
     * @return generic object
     */
    T caseMySql();

    /**
     * Method executed in case if data base type is POSTGRESQL.
     *
     * @return generic object
     */
    T casePostgreSQL();

    /**
     * Method executed in case if data base type is MS_ACCESS.
     *
     * @return generic object
     */
    T caseMSAccess();

    /**
     * Method executed in case if data base type is MSSQL.
     *
     * @return generic object
     */
    T caseMSSQL();

    /**
     * Method executed in case if data base type is SQLite.
     *
     * @return generic object
     */
    T caseSQLite();
}
