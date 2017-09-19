package eca.filter;

/**
 * Data filter dictionary class.
 *
 * @author Roman Batygin
 */
public class FilterDictionary {

    public static final String STRING_ATTR_ERROR_TEXT = "Алгоритм не работает со строковыми атрибутами!";
    public static final String CLASS_NOT_SELECTED_ERROR_TEXT = "Выберите атрибут класса!";
    public static final String BAD_CLASS_TYPE_ERROR_TEXT = "Атрибут класса должен иметь категориальный тип!";
    public static final String BAD_NUMBER_OF_CLASSES_ERROR_TEXT =
            "Атрибут класса должен иметь не менее двух значений!";
    public static final String EMPTY_INSTANCES_ERROR_TEXT =
            "Обучающее множество не содержит объектов с заданными классами!";
}
