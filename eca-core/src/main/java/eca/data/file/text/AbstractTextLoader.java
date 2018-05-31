package eca.data.file.text;

import eca.data.AbstractDataLoader;
import eca.data.file.resource.DataResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eca.util.Utils.isMissing;

/**
 * Abstract class for loading training data represented as text.
 *
 * @author Roman Batygin
 */
public abstract class AbstractTextLoader extends AbstractDataLoader<DataResource> {

    private static final String COLUMNS_SPLIT_REGEX = ",";

    @Override
    public Instances loadInstances() throws IOException {
        List<List<String>> data = readDataFromResource();
        if (CollectionUtils.isEmpty(data)) {
            throw new IllegalArgumentException(String.format("File '%s' has empty data!", getSource().getFile()));
        }
        Instances instances = new Instances(getSource().getFile(), createAttribute(data), data.size());

        for (int i = 1; i < data.size(); i++) {
            DenseInstance newInstance = new DenseInstance(instances.numAttributes());
            newInstance.setDataset(instances);
            for (int j = 0; j < instances.numAttributes(); j++) {
                String val = data.get(i).get(j).trim();
                Attribute attribute = instances.attribute(j);
                if (isMissing(val)) {
                    newInstance.setValue(attribute, Utils.missingValue());
                } else if (attribute.isNumeric()) {
                    newInstance.setValue(attribute, Double.valueOf(val));
                } else {
                    newInstance.setValue(attribute, val);
                }
            }
            instances.add(newInstance);
        }
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    /**
     * Reads data from specified resource.
     *
     * @return data as list
     * @throws IOException
     */
    protected abstract List<List<String>> readDataFromResource() throws IOException;

    protected List<String> parseLine(String line, int columnSize, int rowIdx) {
        String[] row = line.split(COLUMNS_SPLIT_REGEX);
        if (columnSize != 0 && row.length != 0 && row.length != columnSize) {
            throw new IllegalArgumentException(
                    String.format("Invalid columns number at row %d: expected '%d', actual '%d'", rowIdx,
                            columnSize, row.length));
        }
        return Arrays.asList(row);
    }

    private ArrayList<Attribute> createAttribute(List<List<String>> data) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < data.get(0).size(); i++) {
            ArrayList<String> values = new ArrayList<>();
            int attributeType = Attribute.NUMERIC;
            for (int j = 1; j < data.size(); j++) {
                String val = data.get(j).get(i).trim();
                if ((!NumberUtils.isCreatable(val) && !isMissing(val) && !values.contains(val)) ||
                        (!values.isEmpty() && !values.contains(val))) {
                    attributeType = Attribute.NOMINAL;
                    values.add(val);
                }
            }
            attributes.add(createAttribute(attributeType, data.get(0).get(i), values));
        }
        return attributes;
    }

    private Attribute createAttribute(int attributeType, String name, ArrayList<String> values) {
        return attributeType == Attribute.NOMINAL ? new Attribute(name, values) : new Attribute(name);
    }
}
