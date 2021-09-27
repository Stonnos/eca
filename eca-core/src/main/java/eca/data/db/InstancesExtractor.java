package eca.data.db;

import eca.data.db.model.ColumnData;
import eca.data.db.model.InstancesResultSet;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static eca.data.db.SqlTypeUtils.isDate;
import static eca.data.db.SqlTypeUtils.isNominal;
import static eca.data.db.SqlTypeUtils.isNumeric;

/**
 * Class for extracting instances data from sql result sets.
 *
 * @author Roman Batygin
 */
public class InstancesExtractor {

    private static final int RELATION_NAME_INDEX = 1;

    /**
     * Extracts instances set from sql result set.
     *
     * @param resultSet - sql sql result set
     * @return instances set
     * @throws SQLException in case of SQL errors
     */
    public InstancesResultSet extractData(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        checkColumnsTypes(metaData);
        String tableName = metaData.getTableName(RELATION_NAME_INDEX);
        List<ColumnData> columnDataList = getColumnsData(metaData);
        List<List<Object>> dataList = getDataFromResultSet(resultSet, metaData);
        return new InstancesResultSet(tableName, columnDataList, dataList);
    }

    private List<ColumnData> getColumnsData(ResultSetMetaData metaData) throws SQLException {
        List<ColumnData> columnDataList = new ArrayList<>(metaData.getColumnCount());
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnDataList.add(new ColumnData(metaData.getColumnType(i), metaData.getColumnName(i)));
        }
        return columnDataList;
    }

    private List<List<Object>> getDataFromResultSet(ResultSet result, ResultSetMetaData metaData) throws SQLException {
        List<List<Object>> data = new ArrayList<>();
        while (result.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (result.getObject(i) == null) {
                    row.add(null);
                } else if (isNumeric(metaData.getColumnType(i))) {
                    row.add(result.getDouble(i));
                } else if (isDate(metaData.getColumnType(i))) {
                    row.add(getDateValue(result, metaData, i));
                } else {
                    row.add(getStringValue(result, i));
                }
            }
            data.add(row);
        }
        return data;
    }

    private long getDateValue(ResultSet result, ResultSetMetaData metaData, int column) throws SQLException {
        switch (metaData.getColumnType(column)) {
            case Types.DATE:
                return result.getDate(column).getTime();
            case Types.TIME:
                return result.getTime(column).getTime();
            case Types.TIMESTAMP:
                return result.getTimestamp(column).getTime();
            default:
                throw new IllegalArgumentException(
                        String.format(DataBaseDictionary.BAD_COLUMN_TYPE_ERROR_FORMAT,
                                metaData.getColumnTypeName(column)));
        }
    }

    private String getStringValue(ResultSet result, int column) throws SQLException {
        String stringValue = result.getObject(column).toString().trim();
        return !StringUtils.isEmpty(stringValue) ? stringValue : null;
    }

    private void checkColumnsTypes(ResultSetMetaData metaData) throws SQLException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (!isNumeric(metaData.getColumnType(i)) && !isNominal(metaData.getColumnType(i)) &&
                    !isDate(metaData.getColumnType(i))) {
                throw new IllegalArgumentException(
                        String.format(DataBaseDictionary.BAD_COLUMN_TYPE_ERROR_FORMAT, metaData.getColumnTypeName(i)));
            }
        }
    }
}
