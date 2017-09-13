package eca.neural;

/**
 * Neural network dictionary.
 * @author Roman Batygin
 */

public class NeuralNetworkDictionary {

    public static final String IN_LAYER_NEURONS_NUM = "Количество нейронов во входном слое:";
    public static final String OUT_LAYER_NEURONS_NUM = "Количество нейронов в выходном слое:";
    public static final String HIDDEN_LAYER_NUM = "Количество скрытых слоев:";
    public static final String HIDDEN_LAYER_STRUCTURE = "Структура скрытого слоя:";
    public static final String MAX_ITS = "Максимальное число итераций:";
    public static final String ERROR_THRESHOLD = "Допустимая ошибка:";
    public static final String HIDDEN_LAYER_AF = "Активационная функция нейронов скрытого слоя:";
    public static final String HIDDEN_LAYER_AF_COEFFICIENT =
            "Значение коэффициента активационной функции нейронов скрытого слоя:";
    public static final String OUT_LAYER_AF = "Активационная функция нейронов выходного слоя:";
    public static final String OUT_LAYER_AF_COEFFICIENT =
            "Значение коэффициента активационной функции нейронов выходного слоя:";
    public static final String LEARNING_ALGORITHM = "Алгоритм обучения:";

    public static final String LEARNING_SPEED = "Коэффициент скорости обучения:";
    public static final String MOMENTUM = "Коэффициент момента:";

    public static final String AF_COEFFICIENT_VALUE_FORMAT = "Значение коэффициента: %s";

    public static final String BAD_AF_COEFFICIENT_VALUE_ERROR_TEXT = "Значение коэффициента должно быть больше нуля!";

    public static final String BAD_NEURONS_NUM_ERROR_FORMAT = "Число нейронов должно быть больше %d!";

    public static final String BAD_HIDDEN_LAYERS_NUM_ERROR_TEXT
            = "Количество скрытых слоев должно быть не менее одного!";
    public static final String BAD_HIDDEN_LAYER_STRUCTURE = "Неправильный формат структуры скрытого слоя!";
    public static final String BAD_NEURONS_NUM_IN_HIDDEN_LAYER_ERROR_TEXT =
            "Количество нейронов в одном скрытом слое должно быть не менее одного!";

}
