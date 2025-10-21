package eca.model;

import eca.gui.text.DoubleDocument;
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

import static eca.gui.service.ValidationService.isValidDate;
import static eca.gui.service.ValidationService.parseDate;

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

    public Object getTypedValue(int rowIdx, int attrIdx) {
        Object value = values.get(rowIdx).get(attrIdx);
        return getTypedValue(value, attrIdx);
    }

    public void setValue(int rowIdx, int attrIdx, Object value) {
        values.get(rowIdx).set(attrIdx, convertToTypedValue(value));
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
            Object x = getTypedValue(o1.get(columnIndex), columnIndex);
            Object y = getTypedValue(o2.get(columnIndex), columnIndex);
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

    public String getStringValue(Object value, int attrIdx) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer nominalCodeValue) {
            return codeToString(nominalCodeValue, attrIdx);
        } else if (value instanceof Double doubleValue) {
            return decimalFormat.format(doubleValue);
        } else if (value instanceof Date date) {
            return simpleDateFormat.format(date);
        } else {
            return value.toString();
        }
    }

    private Object getTypedValue(Object value, int attrIdx) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer nominalCodeValue) {
            return codeToString(nominalCodeValue, attrIdx);
        } else {
            return value;
        }
    }

    private String codeToString(Integer nominalCodeValue, int attrIdx) {
        String codeStrValue = attributesCodes.get(attrIdx).get(nominalCodeValue);
        Objects.requireNonNull(codeStrValue,
                String.format("Expected not null string value for code [%d]", nominalCodeValue));
        return codeStrValue;
    }

    private Object convertToTypedValue(Object value) {
        try {
            if (value == null) {
                return null;
            } else if (value.toString().matches(DoubleDocument.DOUBLE_FORMAT)) {
                return decimalFormat.parse(value.toString()).doubleValue();
            } else if (isValidDate(value.toString())) {
                return parseDate(value.toString());
            } else {
                return value;
            }
        } catch (ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private int compareAsDate(Object x, Object y) {
        Date dateX = (Date) x;
        Date dateY = (Date) y;
        return dateX.compareTo(dateY);
    }

    private int compareAsNumeric(Object x, Object y) {
        Double numberX = (Double) x;
        Double numberY = (Double) y;
        return Double.compare(numberX, numberY);
    }
}
