package eca.util;

import eca.model.DataSetList;
import lombok.experimental.UtilityClass;
import weka.core.Attribute;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for converting {@link Instances} objects to lists.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class InstancesConverter {

    public static DataSetList convertToDataSet(Instances data,
                                               DecimalFormat decimalFormat,
                                               SimpleDateFormat simpleDateFormat) {
        DataSetList dataSetList = new DataSetList();
        List<String> attributes = IntStream.range(0, data.numAttributes())
                .mapToObj(i -> data.attribute(i).name())
                .collect(Collectors.toList());
        dataSetList.setAttributes(attributes);
        dataSetList.setValues(convertValues(data));
        dataSetList.setAttributesCodes(convertAttributesCodes(data));
        dataSetList.setDecimalFormat(decimalFormat);
        dataSetList.setSimpleDateFormat(simpleDateFormat);
        return dataSetList;
    }

    public static Map<Integer, Map<Integer, String>> convertAttributesCodes(Instances data) {
        Map<Integer, Map<Integer, String>> attributeCodes = new HashMap<>();
        IntStream.range(0, data.numAttributes()).forEach(attrIdx -> {
            Attribute attribute = data.attribute(attrIdx);
            if (attribute.isNominal()) {
                Map<Integer, String> codesMap = new HashMap<>();
                IntStream.range(0, attribute.numValues()).forEach(
                        codeIdx -> codesMap.put(codeIdx, attribute.value(codeIdx))
                );
                attributeCodes.put(attrIdx, codesMap);
            }
        });
        return attributeCodes;
    }

    public static List<List<Object>> convertValues(Instances data) {
        List<List<Object>> values = new ArrayList<>(data.numInstances());
        for (int i = 0; i < data.numInstances(); i++) {
            ArrayList<Object> row = new ArrayList<>(data.numAttributes());
            for (int j = 0; j < data.numAttributes(); j++) {
                Attribute attr = data.instance(i).attribute(j);
                if (data.instance(i).isMissing(attr)) {
                    row.add(null);
                } else if (attr.isDate()) {
                    row.add(new Date((long) data.instance(i).value(j)));
                } else if (attr.isNumeric()) {
                    row.add(data.instance(i).value(j));
                } else {
                    row.add((int) data.instance(i).value(j));
                }
            }
            values.add(row);
        }
        return values;
    }
}
