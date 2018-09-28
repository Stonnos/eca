package eca.data.db;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Result data model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class ResultData {

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

