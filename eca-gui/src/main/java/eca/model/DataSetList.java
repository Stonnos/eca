package eca.model;

import lombok.Getter;
import lombok.Setter;
import weka.core.Attribute;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class DataSetList {

    @Setter
    private List<String> attributes;

    @Setter
    private Map<Integer, Map<Integer, String>> attributesCodes;

    @Setter
    private List<List<Object>> values;

    @Setter
    private DecimalFormat decimalFormat;

    @Setter
    private SimpleDateFormat simpleDateFormat;

    public Object getValue(int rowIdx, int attrIdx) {
        Object value = values.get(rowIdx).get(attrIdx);
        return getStringValue(value, attrIdx);
    }

    public void setValue(int rowIdx, int attrIdx, Object value) {
        values.get(rowIdx).set(attrIdx, value);
    }

    public int size() {
        return values.size();
    }

    public void clear() {
        values.forEach(List::clear);
        values.clear();
        attributesCodes.clear();
    }

    public void remove(int rowIdx) {
        values.remove(rowIdx);
    }

    public void addRow(List<Object> row) {
        values.add(row);
    }

    public void sort(final int columnIndex, final int attributeType, final boolean ascending) {
        values.sort((o1, o2) -> {
            Object x = getStringValue(o1.get(columnIndex), columnIndex);
            Object y = getStringValue(o2.get(columnIndex), columnIndex);
            int sign = ascending ? 1 : -1;
            if (Objects.equals(x, y)) {
                return 0;
            } else if (x == null) {
                return ascending ? sign : -sign;
            } else if (y == null) {
                return ascending ? -sign : sign;
            } else {
                return switch (attributeType) {
                    case Attribute.DATE -> sign * compareAsDate(x, y);
                    case Attribute.NUMERIC -> sign * compareAsNumeric(x, y);
                    case Attribute.NOMINAL -> sign * x.toString().compareTo(y.toString());
                    default -> throw new IllegalArgumentException(
                            String.format("Unexpected attribute type for column index %d!", columnIndex));
                };
            }
        });
    }

    private String getStringValue(Object value, int attrIdx) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer nominalCodeValue) {
            String codeStrValue = attributesCodes.get(attrIdx).get(nominalCodeValue);
            Objects.requireNonNull(codeStrValue,
                    String.format("Expected not null string value for code [%d]", nominalCodeValue));
            return codeStrValue;
        } else if (value instanceof Double doubleValue) {
            return decimalFormat.format(doubleValue);
        } else {
            return value.toString();
        }
    }

    private int compareAsDate(Object x, Object y) {
        try {
            Date dateX = simpleDateFormat.parse(x.toString());
            Date dateY = simpleDateFormat.parse(y.toString());
            return dateX.compareTo(dateY);
        } catch (ParseException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    private int compareAsNumeric(Object x, Object y) {
        try {
            Number numberX = decimalFormat.parse(x.toString());
            Number numberY = decimalFormat.parse(y.toString());
            return Double.compare(numberX.doubleValue(), numberY.doubleValue());
        } catch (ParseException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
}
