package eca.data.file.text;

import eca.data.AbstractDataLoader;
import eca.data.FileUtils;
import eca.data.file.resource.DataResource;
import org.apache.commons.lang3.math.NumberUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eca.util.Utils.isMissing;

/**
 * Implements training data loading from file with extensions such as:
 * - txt
 * - data
 *
 * @author Roman Batygin
 */
public class DATALoader extends AbstractDataLoader<DataResource> {

    private static final String SPLIT_REGEX = ",";
    private static final String UTF_8 = "UTF-8";

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!FileUtils.isTxtExtension(resource.getFile())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

    @Override
    public Instances loadInstances() throws IOException {
        List<List<String>> data = readDataFromResource();
        if (data.isEmpty()) {
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

    private List<List<String>> readDataFromResource() throws IOException {
        List<List<String>> data = new ArrayList<>();
        String line;
        int columnSize = 0;
        int rowIdx = 1;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getSource().openInputStream(), UTF_8))) {
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(SPLIT_REGEX);
                if (columnSize != 0 && row.length != 0 && row.length != columnSize) {
                    throw new IllegalArgumentException(
                            String.format("Invalid columns number at row %d: expected '%d', actual '%d'", rowIdx,
                                    columnSize, row.length));
                }
                columnSize = row.length;
                data.add(Arrays.asList(row));
                rowIdx++;
            }
        }
        return data;
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
