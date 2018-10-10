/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.db;

import eca.data.AbstractDataLoader;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static eca.data.db.SqlTypeUtils.isDate;
import static eca.data.db.SqlTypeUtils.isNominal;
import static eca.data.db.SqlTypeUtils.isNumeric;

/**
 * Implements loading data from database.
 *
 * @author Roman Batygin
 */
public class JdbcQueryExecutor extends AbstractDataLoader<String> implements AutoCloseable {

    /**
     * Default relation name
     **/
    private static final String DEFAULT_RELATION_NAME = "Relation";

    private static final int RELATION_NAME_INDEX = 1;

    /**
     * Database connection object
     **/
    private Connection connection;

    /**
     * Datasource descriptor
     **/
    private ConnectionDescriptor connectionDescriptor;

    /**
     * Opens connection with database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException, ClassNotFoundException {
        Class.forName(connectionDescriptor.getDriver());
        connection = DriverManager.getConnection(connectionDescriptor.getUrl(),
                connectionDescriptor.getLogin(), connectionDescriptor.getPassword());
    }


    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Returns <tt>ConnectionDescriptor</tt> object.
     *
     * @return <tt>ConnectionDescriptor</tt> object
     */
    public ConnectionDescriptor getConnectionDescriptor() {
        return connectionDescriptor;
    }

    /**
     * Sets <tt>ConnectionDescriptor</tt> object.
     *
     * @param connectionDescriptor <tt>ConnectionDescriptor</tt> object
     */
    public void setConnectionDescriptor(ConnectionDescriptor connectionDescriptor) {
        Objects.requireNonNull(connectionDescriptor, "Connection descriptor is not specified!");
        Objects.requireNonNull(connectionDescriptor.getDriver(), "Driver is not specified!");
        this.connectionDescriptor = connectionDescriptor;
    }

    @Override
    public Instances loadInstances() throws Exception {
        Instances data;
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(getSource())) {
            ResultData resultData = getResultData(result);
            data = new Instances(
                    StringUtils.isEmpty(resultData.getTableName()) ? DEFAULT_RELATION_NAME : resultData.getTableName(),
                    createAttributes(resultData), result.getFetchSize());

            for (List<Object> row : resultData.getData()) {
                Instance obj = new DenseInstance(data.numAttributes());
                obj.setDataset(data);
                for (int i = 0; i < resultData.getColumnData().size(); i++) {
                    Attribute attribute = data.attribute(i);
                    if (row.get(i) == null) {
                        obj.setValue(attribute, Utils.missingValue());
                    } else if (attribute.isDate()) {
                        obj.setValue(attribute, (long) row.get(i));
                    } else if (attribute.isNumeric()) {
                        obj.setValue(attribute, (double) row.get(i));
                    } else {
                        obj.setValue(attribute, row.get(i).toString());
                    }
                }
                data.add(obj);
            }

        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
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

    private ArrayList<Attribute> createAttributes(ResultData resultData) {
        ArrayList<Attribute> attr = new ArrayList<>(resultData.getColumnData().size());
        for (int i = 0; i < resultData.getColumnData().size(); i++) {
            ColumnData columnData = resultData.getColumnData().get(i);
            if (isNumeric(columnData.getType())) {
                attr.add(new Attribute(columnData.getName()));
            } else if (canHandleAsDate(columnData.getType())) {
                attr.add(new Attribute(columnData.getName(), getDateFormat()));
            } else {
                attr.add(new Attribute(columnData.getName(), createNominalAttribute(resultData.getData(), i)));
            }
        }
        return attr;
    }

    private ArrayList<String> createNominalAttribute(List<List<Object>> dataList, int i) {
        ArrayList<String> values = new ArrayList<>();
        dataList.forEach(row -> {
            Object value = row.get(i);
            if (value != null) {
                String trimValue = value.toString().trim();
                if (!StringUtils.isEmpty(trimValue) && !values.contains(trimValue)) {
                    values.add(trimValue);
                }
            }
        });
        return values;
    }

    private ResultData getResultData(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        checkColumnsTypes(metaData);
        String tableName = metaData.getTableName(RELATION_NAME_INDEX);
        List<ColumnData> columnDataList = getColumnsData(metaData);
        List<List<Object>> dataList = getDataFromResultSet(resultSet, metaData);
        return new ResultData(tableName, columnDataList, dataList);
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
                } else if (canHandleAsDate(metaData.getColumnType(i))) {
                    switch (metaData.getColumnType(i)) {
                        case Types.DATE:
                            row.add(result.getDate(i).getTime());
                            break;
                        case Types.TIME:
                            row.add(result.getTime(i).getTime());
                            break;
                        case Types.TIMESTAMP:
                            row.add(result.getTimestamp(i).getTime());
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    String.format(DataBaseDictionary.BAD_COLUMN_TYPE_ERROR_FORMAT,
                                            metaData.getColumnTypeName(i)));
                    }
                } else {
                    String stringValue = result.getObject(i).toString().trim();
                    if (!StringUtils.isEmpty(stringValue)) {
                        row.add(stringValue);
                    } else {
                        row.add(null);
                    }
                }
            }
            data.add(row);
        }
        return data;
    }

    private boolean canHandleAsDate(int columnType) {
        return !DataBaseType.SQLITE.equals(connectionDescriptor.getDataBaseType()) && isDate(columnType);
    }
}
