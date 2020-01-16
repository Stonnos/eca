package eca.metrics;

import lombok.experimental.UtilityClass;

/**
 * K - nearest neighbours dictionary class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class KNNDictionary {

    public static final String NEIGHBOURS_NUM = "Число ближайших соседей:";
    public static final String NEIGHBOUR_WEIGHT = "Вес ближайшего соседа:";
    public static final String DISTANCE_FUNCTION = "Функция расстояния:";

    public static final String BAD_NEIGHBOURS_NUM_ERROR_FORMAT = "Число ближайших соседей должно быть не менее %d!";
    public static final String BAD_WEIGHT_ERROR_FORMAT = "Вес должен лежать в интервале [%.1f, %.1f]!";
}
