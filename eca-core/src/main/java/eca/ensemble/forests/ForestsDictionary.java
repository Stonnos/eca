package eca.ensemble.forests;

import lombok.experimental.UtilityClass;

/**
 * Random forests dictionary class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ForestsDictionary {

    public static final String DECISION_TREE_ALGORITHM = "Алгоритм построения дерева решений:";
    public static final String NUM_TREES = "Число деревьев:";

    public static final String INVALID_NUM_RANDOM_SPLITS_ERROR_MESSAGE_FORMAT =
            "Число случайных расщеплений атрибута должно быть не менее %d!";
}
