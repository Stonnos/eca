package eca.data.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Column data model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class ColumnData {

    /**
     * Column type
     */
    private int type;

    /**
     * Column name
     */
    private String name;
}
