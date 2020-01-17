package eca.gui.service;

import eca.config.ConfigurationService;
import eca.gui.dictionary.CommonDictionary;
import eca.text.NumericFormatFactory;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
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

    private static final String INCORRECT_DATE_VALUES_ERROR_FORMAT =
            "Формат даты для атрибута '%s' должен быть следующим: %s";
    private static final String NUMERIC_OVERFLOW_ERROR_FORMAT =
            "Для числового атрибута '%s' найдено слишком большое значение!\nДлина целой части не должна превышать %d знаков!";

    public static void isNumericOverflow(String attribute, String val) {
        int delimiterIndex = val.lastIndexOf(NumericFormatFactory.DECIMAL_SEPARATOR);
        int length = delimiterIndex < 0 ? val.length() : delimiterIndex;
        if (length > CommonDictionary.MAXIMUM_INTEGER_DIGITS) {
            throw new IllegalArgumentException(
                    String.format(NUMERIC_OVERFLOW_ERROR_FORMAT, attribute, CommonDictionary.MAXIMUM_INTEGER_DIGITS));
        }
    }

    public static Date parseDate(String attribute, String val) {
        try {
            return SIMPLE_DATE_FORMAT.parse(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(INCORRECT_DATE_VALUES_ERROR_FORMAT,
                    attribute, CONFIG_SERVICE.getApplicationConfig().getDateFormat()));
        }
    }
}
