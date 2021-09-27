package eca.data.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Instances result set model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class InstancesResultSet {

    /**
     * Table name
     */
    private String tableName;

    /**
     * Columns info data
     */
    private List<ColumnData> columnData;

    /**
     * Fetched data
     */
    private List<List<Object>> data;
}

