package eca.data.migration.util;

import eca.data.db.SqlQueryHelper;
import eca.data.db.SqlTypeUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static eca.data.db.SqlQueryHelper.normalizeName;
import static eca.data.db.SqlQueryHelper.truncateStringValue;

/**
 * Unit tests for checking {@link SqlQueryHelper} functionality.
 *
 * @author Roman Batygin
 */
public class UtilsTest {

    private static final int VARCHAR_LENGTH = 255;
    private static final String STRING_VALUE_FORMAT = "'%s'";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String COLUMN_FORMAT = "%s %s";
    private static final String NUMERIC_TYPE = "NUMERIC(22,9)";
    private static final String VARCHAR_TYPE = "VARCHAR(255)";
    private static final String TIMESTAMP_FORMAT = "TIMESTAMP";
    private static final String NULL_VALUE = "NULL";

    private SqlQueryHelper sqlQueryHelper = new SqlQueryHelper();

    @Before
    public void init() {
        sqlQueryHelper.setDateColumnType(SqlTypeUtils.TIMESTAMP_TYPE);
    }

    @Test
    public void testNormalizeName() {
        String name = "*Фяabc _!$%@@##12-gdgd&?()хжЁ+э\n\t";
        String result = "_фяabc_________12_gdgd____хжё_э__";
        Assertions.assertThat(normalizeName(name)).isEqualTo(result);
    }

    /**
     * Tests truncate value.
     * Case 1. Value length is greater than 255
     * Case 2. Value length is less than 255
     */
    @Test
    public void testTruncateValue() {
        //Case 1
        String value = RandomStringUtils.random(VARCHAR_LENGTH + 1);
        String expected = value.substring(0, VARCHAR_LENGTH);
        String actual = truncateStringValue(value);
        Assertions.assertThat(actual.length()).isEqualTo(VARCHAR_LENGTH);
        Assertions.assertThat(actual).isEqualTo(expected);
        //Case 2
        value = RandomStringUtils.random(VARCHAR_LENGTH - 1);
        Assertions.assertThat(truncateStringValue(value)).isEqualTo(value);
    }

    /**
     * Tests format attribute to column type in create table command.
     * Case 1: Tests numeric attribute
     * Case 2: Tests nominal attribute
     * Case 3: Tests date attribute
     */
    @Test
    public void testFormatAttribute() {
        //Case 1
        Attribute attribute = new Attribute("numericAttribute");
        Assertions.assertThat(sqlQueryHelper.formatAttribute(attribute, COLUMN_FORMAT)).isEqualTo(
                String.format(COLUMN_FORMAT, attribute.name().toLowerCase(), NUMERIC_TYPE));
        //Case 2
        attribute = new Attribute("nominalAttribute", Arrays.asList("A", "B", "C"));
        Assertions.assertThat(sqlQueryHelper.formatAttribute(attribute, COLUMN_FORMAT)).isEqualTo(
                String.format(COLUMN_FORMAT, attribute.name().toLowerCase(), VARCHAR_TYPE));
        //Case 3
        attribute = new Attribute("dateAttribute", DATE_FORMAT);
        Assertions.assertThat(sqlQueryHelper.formatAttribute(attribute, COLUMN_FORMAT)).isEqualTo(
                String.format(COLUMN_FORMAT, attribute.name().toLowerCase(), TIMESTAMP_FORMAT));
    }

    /**
     * Tests format value to column value in insert command.
     * Case 1: Tests numeric attribute
     * Case 2: Tests date attribute
     * Case 3: Tests nominal attribute
     * Case 4: Tests missing value
     */
    @Test
    public void testFormatValue() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        Attribute numericAttribute = new Attribute("numericAttribute");
        attributes.add(numericAttribute);
        Attribute dateAttribute = new Attribute("dateAttribute", DATE_FORMAT);
        attributes.add(dateAttribute);
        Attribute nominalAttribute = new Attribute("nominalAttribute", Arrays.asList("A", "B", "C"));
        attributes.add(nominalAttribute);
        Instances instances = new Instances("Relation", attributes, 10);
        instances.setClassIndex(instances.numAttributes() - 1);
        Instance instance = new DenseInstance(instances.numAttributes());
        instance.setValue(numericAttribute, 1);
        instance.setValue(nominalAttribute, "B");
        Date dateValue = new Date();
        instance.setValue(dateAttribute, dateValue.getTime());
        //Case 1
        Assertions.assertThat(sqlQueryHelper.formatValue(instance, numericAttribute)).isEqualTo(
                String.valueOf(instance.value(numericAttribute)));
        //Case 2
        Assertions.assertThat(sqlQueryHelper.formatValue(instance, dateAttribute)).isEqualTo(
                String.format(STRING_VALUE_FORMAT, instance.stringValue(dateAttribute)));
        //Case 3
        Assertions.assertThat(sqlQueryHelper.formatValue(instance, nominalAttribute)).isEqualTo(
                String.format(STRING_VALUE_FORMAT, instance.stringValue(nominalAttribute)));
        //Case 4
        instance.setValue(numericAttribute, weka.core.Utils.missingValue());
        Assertions.assertThat(sqlQueryHelper.formatValue(instance, numericAttribute)).isEqualTo(NULL_VALUE);
    }
}
