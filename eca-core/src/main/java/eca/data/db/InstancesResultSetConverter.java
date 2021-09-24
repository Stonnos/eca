package eca.data.db;

import eca.data.db.model.ColumnData;
import eca.data.db.model.InstancesResultSet;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.List;

import static eca.data.db.SqlTypeUtils.isDate;
import static eca.data.db.SqlTypeUtils.isNumeric;

/**
 * Implements instances results set converter.
 *
 * @author Roman Batygin
 */
public class InstancesResultSetConverter {

    /**
     * Default relation name
     **/
    private static final String DEFAULT_RELATION_NAME = "Relation";

    /**
     * Date format
     */
    @Getter
    @Setter
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Converts instances result set to instances internal model.
     *
     * @param instancesResultSet - instances result set
     * @return instances internal model
     */
    public Instances convert(InstancesResultSet instancesResultSet) {
        String tableName = StringUtils.isEmpty(instancesResultSet.getTableName()) ? DEFAULT_RELATION_NAME :
                instancesResultSet.getTableName();
        ArrayList<Attribute> attributes = createAttributes(instancesResultSet);
        Instances instances = new Instances(tableName, attributes, instancesResultSet.getData().size());

        for (List<Object> row : instancesResultSet.getData()) {
            Instance obj = new DenseInstance(instances.numAttributes());
            obj.setDataset(instances);
            for (int i = 0; i < instancesResultSet.getColumnData().size(); i++) {
                Attribute attribute = instances.attribute(i);
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
            instances.add(obj);
        }
        return instances;
    }

    private ArrayList<Attribute> createAttributes(InstancesResultSet instancesResultSet) {
        ArrayList<Attribute> attr = new ArrayList<>(instancesResultSet.getColumnData().size());
        for (int i = 0; i < instancesResultSet.getColumnData().size(); i++) {
            ColumnData columnData = instancesResultSet.getColumnData().get(i);
            if (isNumeric(columnData.getType())) {
                attr.add(new Attribute(columnData.getName()));
            } else if (isDate(columnData.getType())) {
                attr.add(new Attribute(columnData.getName(), dateFormat));
            } else {
                attr.add(new Attribute(columnData.getName(), createNominalAttribute(instancesResultSet.getData(), i)));
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
}
