package eca.config;

import lombok.Data;

/**
 * Database test case.
 *
 * @author Roman Batygin
 */
@Data
public class DbTestData {

    /**
     * Expected sql query
     */
    private String sqlQuery;

    /**
     * Expected data file
     */
    private String expectedDataFile;
}
