package eca.util;

import lombok.experimental.UtilityClass;
import weka.core.Attribute;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for converting {@link Instances} objects to lists.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class InstancesConverter {

    /**
     * Converts <tt>Instances</tt> object to list.
     *
     * @param data   <tt>Instances</tt>
     * @param format <tt>DecimalFormat</tt> object
     * @param simpleDateFormat <tt>SimpleDateFormat</tt> object
     * @return list representation of <tt>Instances</tt> object
     */
    public static List<List<Object>> toArray(Instances data, DecimalFormat format,
                                             SimpleDateFormat simpleDateFormat) {
        List<List<Object>> values = new ArrayList<>(data.numInstances());
        for (int i = 0; i < data.numInstances(); i++) {
            ArrayList<Object> row = new ArrayList<>(data.numAttributes());
            for (int j = 0; j < data.numAttributes(); j++) {
                Attribute attr = data.instance(i).attribute(j);
                if (data.instance(i).isMissing(attr)) {
                    row.add(null);
                } else if (attr.isDate()) {
                    row.add(simpleDateFormat.format(new Date((long) data.instance(i).value(j))));
                } else {
                    row.add(attr.isNumeric() ? format.format(data.instance(i).value(j))
                            : data.instance(i).stringValue(j));
                }
            }
            values.add(row);
        }
        return values;
    }

}
