package eca.gui.service;

import eca.config.ConfigurationService;
import eca.gui.dictionary.CommonDictionary;
import eca.text.NumericFormatFactory;
import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * @author Roman Batygin
 */
@UtilityClass
public class ValidationService {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(CONFIG_SERVICE.getApplicationConfig().getDateFormat());

    private static final String INCORRECT_DATE_VALUES_ERROR_FORMAT =
            "Формат даты для атрибута '%s' в строке %d должен быть следующим: %s";
    private static final String NUMERIC_OVERFLOW_ERROR_FORMAT =
            "Для числового атрибута '%s' найдено слишком большое значение в строке %d!\nДлина целой части не должна превышать %d знаков!";

    public static void isNumericOverflow(String attribute, String val, int row) {
        int delimiterIndex = val.lastIndexOf(NumericFormatFactory.DECIMAL_SEPARATOR);
        int length = delimiterIndex < 0 ? val.length() : delimiterIndex;
        if (length > CommonDictionary.MAXIMUM_INTEGER_DIGITS) {
            throw new IllegalArgumentException(
                    String.format(NUMERIC_OVERFLOW_ERROR_FORMAT, attribute, row, CommonDictionary.MAXIMUM_INTEGER_DIGITS));
        }
    }

    public static Date parseDate(String attribute, String val, int row) {
        try {
            return SIMPLE_DATE_FORMAT.parse(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(INCORRECT_DATE_VALUES_ERROR_FORMAT,
                    attribute, row, CONFIG_SERVICE.getApplicationConfig().getDateFormat()));
        }
    }

    public static Date parseDate(String val) {
        try {
            return SIMPLE_DATE_FORMAT.parse(val);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static boolean isValidDate(String value) {
        try {
            DATE_TIME_FORMATTER.parse(value);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
