package eca.gui.service;

import eca.dataminer.AbstractExperiment;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.RandomForests;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.trees.DecisionTreeClassifier;
import lombok.experimental.UtilityClass;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Experiment names factory.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ExperimentNamesFactory {

    public static final String DATA_MINER_NETWORKS = "Автоматическое построение: нейронные сети";
    public static final String DATA_MINER_HETEROGENEOUS_ENSEMBLE =
            "Автоматическое построение: неоднородный ансамблевый алгоритм";
    public static final String DATA_MINER_MODIFIED_HETEROGENEOUS_ENSEMBLE =
            "Автоматическое построение: модифицированный неоднородный ансамблевый алгоритм";
    public static final String DATA_MINER_ADA_BOOST = "Автоматическое построение: алгоритм AdaBoost";
    public static final String DATA_MINER_STACKING = "Автоматическое построение: алгоритм Stacking";
    public static final String DATA_MINER_RANDOM_FORESTS = "Автоматическое построение: случайные леса";
    public static final String DATA_MINER_DECISION_TREE = "Автоматическое построение: деревья решений";
    public static final String DATA_MINER_KNN =
            "Автоматическое построение: алгоритм k - взвешенных ближайших соседей";

    private static final Map<Class<?>, String> EXPERIMENT_TITLES_MAP = newHashMap();

    static {
        EXPERIMENT_TITLES_MAP.put(DecisionTreeClassifier.class, DATA_MINER_DECISION_TREE);
        EXPERIMENT_TITLES_MAP.put(NeuralNetwork.class, DATA_MINER_NETWORKS);
        EXPERIMENT_TITLES_MAP.put(KNearestNeighbours.class, DATA_MINER_KNN);
        EXPERIMENT_TITLES_MAP.put(HeterogeneousClassifier.class, DATA_MINER_HETEROGENEOUS_ENSEMBLE);
        EXPERIMENT_TITLES_MAP.put(ModifiedHeterogeneousClassifier.class, DATA_MINER_MODIFIED_HETEROGENEOUS_ENSEMBLE);
        EXPERIMENT_TITLES_MAP.put(AdaBoostClassifier.class, DATA_MINER_ADA_BOOST);
        EXPERIMENT_TITLES_MAP.put(StackingClassifier.class, DATA_MINER_STACKING);
        EXPERIMENT_TITLES_MAP.put(RandomForests.class, DATA_MINER_RANDOM_FORESTS);
    }

    /**
     * Gets experiment name.
     *
     * @param experiment - experiment object
     * @return experiment name
     */
    public static String getExperimentName(AbstractExperiment<?> experiment) {
        return EXPERIMENT_TITLES_MAP.entrySet().stream()
                .filter(entry -> experiment.getClass().isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Can't get experiment [%s] name",
                        experiment.getClassifier().getClass().getSimpleName())));
    }
}
