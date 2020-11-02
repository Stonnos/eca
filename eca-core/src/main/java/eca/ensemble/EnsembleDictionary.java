package eca.ensemble;

import lombok.experimental.UtilityClass;

/**
 * Ensemble algorithms dictionary class,
 *
 * @author Roman Batygin
 */
@UtilityClass
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
    public static final String NUM_FOLDS = "Число блоков:";

    public static final String META_CLASSIFIER = "Мета-классификатор:";
    public static final String META_SAMPLING_METHOD = "Метод формирования мета-признаков:";
    public static final String TRAINING_SET_METHOD = "Использование обучающего множества";
    public static final String CROSS_VALIDATION = "%d - блочная кросс-проверка";

    public static final String NUM_THREADS = "Число потоков:";

    public static final String NETWORK_MIN_ERROR = "Минимальная допустимая ошибка сети:";
    public static final String NETWORK_MAX_ERROR = "Максимальная допустимая ошибка сети:";

    public static final String EMPTY_ENSEMBLE_ERROR_TEXT =
            "Не удалось построить модель: ни один классификатор не был включен в ансамбль!";

    public static final String INVALID_ERROR_THRESHOLD_TEXT =
            "Значение допустимой ошибки классификатора должно лежать в интервале: [%.1f, %.1f]!";

    public static final String INVALID_NUM_ITS_ERROR_FORMAT = "Число итераций должно быть не менее %d!";
    public static final String INVALID_NUM_THREADS_ERROR_FORMAT = "Число потоков должно быть не менее %d!";
    public static final String SEED = "Начальное значение (Seed):";
}
