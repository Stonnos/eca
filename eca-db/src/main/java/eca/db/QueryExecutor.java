package eca.db;

import weka.core.Instances;

/**
 * Interface for query execution.
 *
 * @author Roman Batygin
 */
public interface QueryExecutor {

    /**
     * Returns <code>Instances</code> object formed as query result.
     *
     * @param query query string of <code>SELECT</code> type
     * @return {@link Instances} object formed as query result
     * @throws Exception
     */
    Instances executeQuery(String query) throws Exception;

}
