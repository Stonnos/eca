package eca.core;

import eca.filter.MissingValuesFilter;

/**
 * Interface for filtering training set.
 *
 * @author Roman Batygin
 */
public interface FilterHandler {

    /**
     * Returns missing values filter.
     *
     * @return missing values filter.
     */
    MissingValuesFilter getFilter();

}
