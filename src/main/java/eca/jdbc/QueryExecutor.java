package eca.jdbc;

import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public interface QueryExecutor {

    Instances executeQuery(String query) throws Exception;

}
