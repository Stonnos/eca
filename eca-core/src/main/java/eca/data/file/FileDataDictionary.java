package eca.data.file;

import lombok.experimental.UtilityClass;

/**
 * Data module dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FileDataDictionary {

    public static final String EMPTY_COLUMNS_ERROR = "Данные должны быть без пустых столбцов!";
    public static final String EMPTY_DATASET_ERROR = "Данные должны содержать хотя бы одну строку!";
    public static final String BAD_DATA_FORMAT = "Данные не должны содержать пустые строки!";
    public static final String BAD_CELL_VALUES = "Значения должны быть числовыми или текстовыми!";
    public static final String DIFFERENT_DATA_TYPES_IN_COLUMN_ERROR_FORMAT =
            "Столбец %d содержит данные различных типов!";
    public static final String HEADER_ERROR = "Заданы не все имена атрибутов!";
    public static final String BAD_FILE_EXTENSION_ERROR_FORMAT =
            "Допускаются только файлы форматов: %s!";
    public static final String BAD_PROTOCOL_ERROR_FORMAT =
            "Протокол соединения должен быть одни из следующих %s!";

}
