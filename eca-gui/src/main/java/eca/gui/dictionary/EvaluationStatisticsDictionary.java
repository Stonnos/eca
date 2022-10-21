package eca.gui.dictionary;

import lombok.experimental.UtilityClass;

/**
 * @author Roman Batygin
 */
@UtilityClass
public class EvaluationStatisticsDictionary {

    public static final String INITIAL_DATA_TEXT = "Исходные данные";
    public static final String NUMBER_OF_INSTANCES_TEXT = "Число объектов";
    public static final String NUMBER_OF_ATTRIBUTES_TEXT = "Число атрибутов";
    public static final String NUMBER_OF_CLASSES_TEXT = "Число классов";
    public static final String CLASSIFIER_NAME_TEXT = "Классификатор";
    public static final String EVALUATION_METHOD_TEXT = "Метод оценки точности";
    public static final String TRAINING_DATA_METHOD_TEXT = "Использование обучающей выборки";
    public static final String NUMBER_OF_TEST_INSTANCES = "Число объектов тестовых данных";
    public static final String CORRECTLY_CLASSIFIED_INSTANCES_TEXT = "Число правильно классифицированных объектов";
    public static final String INCORRECTLY_CLASSIFIED_INSTANCES_TEXT = "Число неправильно классифицированных объектов";
    public static final String CLASSIFIER_ACCURACY_TEXT = "Точность классификатора, %";
    public static final String CLASSIFIER_ERROR_TEXT = "Ошибка классификатора, %";
    public static final String CLASSIFIER_MEAN_ERROR_TEXT = "Средняя абсолютная ошибка классификации";
    public static final String ROOT_MEAN_SQUARED_ERROR_TEXT = "Среднеквадратическая ошибка классификации";
    public static final String VARIANCE_ERROR_TEXT = "Дисперсия ошибки классификатора";
    public static final String ERROR_CONFIDENCE_INTERVAL_ERROR_TEXT =
            "95% доверительный интервал ошибки классификатора";
    public static final String SEED_TEXT = "Начальное значение (Seed) для k*V блочной кросс проверки";
    public static final String NUMBER_OF_NODES_TEXT = "Число узлов";
    public static final String NUMBER_OF_LEAVES_TEXT = "Число листьев";
    public static final String TREE_DEPTH_TEXT = "Глубина дерева";
    public static final String CLASSIFIERS_IN_ENSEMBLE_TEXT = "Число классификаторов в ансамбле";
    public static final String DISTANCE_FUNCTION_TEXT = "Функция расстояния";
    public static final String META_CLASSIFIER_NAME_TEXT = "Мета-классификатор";
    public static final String IN_LAYER_NEURONS_NUM_TEXT = "Число нейронов во входном слое";
    public static final String OUT_LAYER_NEURONS_NUM_TEXT = "Число нейронов в выходном слое";
    public static final String HIDDEN_LAYERS_NUM_TEXT = "Число скрытых слоев";
    public static final String HIDDEN_LAYER_STRUCTURE_TEXT = "Структура скрытого слоя";
    public static final String LINKS_NUM_TEXT = "Число связей";
    public static final String ACTIVATION_FUNCTION_HIDDEN_LAYER_TEXT = "Активационная функция нейронов скрытого слоя";
    public static final String ACTIVATION_FUNCTION_OUT_LAYER_TEXT = "Активационная функция нейронов выходного слоя";
    public static final String LEARNING_ALGORITHM_TEXT = "Алгоритм обучения";
    public static final String CROSS_VALIDATION_METHOD_FORMAT = "Кросс - проверка, %s%d - блочная";
    public static final String TOTAL_TIME_TEXT = "Затраченное время";
    public static final String INTERVAL_FORMAT = "[%s; %s]";
    public static final String BACK_PROPAGATION_METHOD_TEXT = "Алгоритм обратного распространения ошибки";
}
