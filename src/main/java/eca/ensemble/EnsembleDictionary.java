package eca.ensemble;

/**
 * Ensemble algorithms dictionary class,
 * @author Roman Batygin
 */

public class EnsembleDictionary {

    public static final String NUM_ITS = "Число итераций:";
    public static final String MIN_ERROR = "Минимальная допустимая ошибка классификатора:";
    public static final String MAX_ERROR = "Максимальная допустимая ошибка классификатора:";
    public static final String VOTING_METHOD = "Метод голосования:";
    public static final String WEIGHTED_VOTING = "Метод взвешенного голосования";
    public static final String MAJORITY_VOTING = "Метод большинства голосов";
    public static final String SAMPLING_METHOD = "Формирование обучающих выборок:";
    public static final String CLASSIFIER_SELECTION = "Выбор классификатора:";
    public static final String RANDOM_CLASSIFIER = "Случайный классификатор";
    public static final String OPTIMAL_CLASSIFIER = "Оптимальный классификатор";
    public static final String INDIVIDUAL_CLASSIFIER_FORMAT = "Базовый классификатор %d:";
    public static final String BOOTSTRAP_SAMPLE_METHOD = "Бутстрэп-выборки";
    public static final String TRAINING_SAMPLE_METHOD = "Исходное обучающее множество";

    public static final String META_CLASSIFIER = "Мета-классификатор:";
    public static final String META_SAMPLING_METHOD = "Метод формирования мета-признаков:";
    public static final String TRAINING_SET_METHOD = "Использование обучающего множества";
    public static final String CROSS_VALIDATION = "%d - блочная кросс-проверка";

    public static final String NETWORK_MIN_ERROR = "Минимальная допустимая ошибка сети:";
    public static final String NETWORK_MAX_ERROR = "Максимальная допустимая ошибка сети:";

    public static final String EMPTY_ENSEMBLE_ERROR_TEXT =
            "Не удалось построить модель: ни один классификатор не был включен в ансамбль!";

}
