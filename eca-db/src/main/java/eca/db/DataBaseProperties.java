package eca.db;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * Data base properties.
 *
 * @author Roman Batygin
 */
@Slf4j
public class DataBaseProperties {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final String MYSQL_DRIVER_PROPERTY = "mysql.driver";
    private static final String POSTGRES_DRIVER_PROPERTY = "postgres.driver";
    private static final String ORACLE_DRIVER_PROPERTY = "oracle.driver";
    private static final String MSSQL_DRIVER_PROPERTY = "mssql.driver";
    private static final String MSACCESS_DRIVER_PROPERTY = "msaccess.driver";
    private static final String SQLITE_DRIVER_PROPERTY = "sqlite.driver";

    private static Properties PROPERTIES = new Properties();

    private static DataBaseProperties INSTANCE;

    static {
        try (InputStream stream = DataBaseProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            PROPERTIES.load(stream);
        } catch (Exception ex) {
            log.error("Can't load data base properties:", ex);
        }
    }

    private DataBaseProperties() {
    }

    /**
     * Returns <tt>DataBaseProperties</tt> instance.
     *
     * @return <tt>DataBaseProperties</tt> instance
     */
    public static DataBaseProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataBaseProperties();
        }
        return INSTANCE;
    }

    /**
     * Returns MS Access driver path.
     *
     * @return MS Access driver path
     */
    public String getMsAccessDriverProperty() {
        return PROPERTIES.getProperty(MSACCESS_DRIVER_PROPERTY);
    }

    /**
     * Returns MySQL driver path.
     *
     * @return MySQL driver path
     */
    public String getMysqlDriverProperty() {
        return PROPERTIES.getProperty(MYSQL_DRIVER_PROPERTY);
    }

    /**
     * Returns Oracle driver path.
     *
     * @return Oracle driver path
     */
    public String getOracleDriverProperty() {
        return PROPERTIES.getProperty(ORACLE_DRIVER_PROPERTY);
    }

    /**
     * Returns Postgres driver path.
     *
     * @return Postgres driver path
     */
    public String getPostgresDriverProperty() {
        return PROPERTIES.getProperty(POSTGRES_DRIVER_PROPERTY);
    }

    /**
     * Returns SQLite driver path.
     *
     * @return SQlite driver path
     */
    public String getSqliteDriverProperty() {
        return PROPERTIES.getProperty(SQLITE_DRIVER_PROPERTY);
    }

    public String getMssqlDriverProperty() {
        return PROPERTIES.getProperty(MSSQL_DRIVER_PROPERTY);
    }
}
