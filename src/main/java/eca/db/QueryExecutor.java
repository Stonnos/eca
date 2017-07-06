package eca.db;

import weka.core.Instances;

/**
 * Interface for query executing.
 * @author Roman Batygin
 */
public interface QueryExecutor {

    /**
     * Returns <tt>Instances</tt> object formed as query result.
     * @param query query string of <tt>SELECT</tt> type
     * @return <tt>Instances</tt> object formed as query result
     * @throws Exception
     */
    Instances executeQuery(String query) throws Exception;

}
