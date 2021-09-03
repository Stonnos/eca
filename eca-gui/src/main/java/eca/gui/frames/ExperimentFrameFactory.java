package eca.gui.frames;

import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedDecisionTree;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AutomatedKNearestNeighbours;
import eca.dataminer.AutomatedNeuralNetwork;
import eca.dataminer.AutomatedRandomForests;
import eca.dataminer.AutomatedStacking;
import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Experiment frames helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ExperimentFrameFactory {

    private static final Map<Class<?>, ExperimentFrameSupplier> EXPERIMENT_FRAME_SUPPLIERS = newHashMap();

    static {
        EXPERIMENT_FRAME_SUPPLIERS.put(AutomatedDecisionTree.class,
                new AutomatedDecisionTreeExperimentFrameSupplier());
        EXPERIMENT_FRAME_SUPPLIERS.put(AutomatedNeuralNetwork.class,
                new AutomatedNeuralNetworkExperimentFrameSupplier());
        EXPERIMENT_FRAME_SUPPLIERS.put(AutomatedKNearestNeighbours.class,
                new AutomatedKnnExperimentFrameSupplier());
        EXPERIMENT_FRAME_SUPPLIERS.put(AutomatedRandomForests.class,
                new AutomatedRandomForestsExperimentFrameSupplier());
        EXPERIMENT_FRAME_SUPPLIERS.put(AutomatedHeterogeneousEnsemble.class,
                new AutomatedHecExperimentFrameSupplier());
        EXPERIMENT_FRAME_SUPPLIERS.put(AutomatedStacking.class,
                new AutomatedStackingExperimentFrameSupplier());
    }

    /**
     * Gets experiment frame for specified experiment.
     *
     * @param experiment - experiment model
     * @param parent     - parent frame
     * @param digits     - maximum fraction digits
     * @return experiment frame
     */
    @SuppressWarnings("unchecked")
    public static ExperimentFrame<?> getExperimentFrame(AbstractExperiment<?> experiment, JFrame parent, int digits) {
        ExperimentFrameSupplier experimentFrameSupplier =
                EXPERIMENT_FRAME_SUPPLIERS.get(experiment.getClass());
        if (experimentFrameSupplier == null) {
            throw new IllegalStateException(String.format("Can't create experiment [%s] frame",
                    experiment.getClass().getSimpleName()));
        }
        return experimentFrameSupplier.get(experiment, parent, digits);
    }

    @FunctionalInterface
    private interface ExperimentFrameSupplier<T extends AbstractExperiment<?>, S extends ExperimentFrame<T>> {
        S get(T experiment, JFrame parent, int digits);
    }

    private static class AutomatedDecisionTreeExperimentFrameSupplier
            implements ExperimentFrameSupplier<AutomatedDecisionTree, AutomatedDecisionTreeFrame> {

        @Override
        public AutomatedDecisionTreeFrame get(AutomatedDecisionTree experiment, JFrame parent, int digits) {
            return new AutomatedDecisionTreeFrame("", experiment, parent, digits);
        }
    }

    private static class AutomatedNeuralNetworkExperimentFrameSupplier
            implements ExperimentFrameSupplier<AutomatedNeuralNetwork, AutomatedNeuralNetworkFrame> {

        @Override
        public AutomatedNeuralNetworkFrame get(AutomatedNeuralNetwork experiment, JFrame parent, int digits) {
            return new AutomatedNeuralNetworkFrame(experiment, parent, digits);
        }
    }

    private static class AutomatedKnnExperimentFrameSupplier
            implements ExperimentFrameSupplier<AutomatedKNearestNeighbours, AutomatedKNearestNeighboursFrame> {

        @Override
        public AutomatedKNearestNeighboursFrame get(AutomatedKNearestNeighbours experiment, JFrame parent, int digits) {
            return new AutomatedKNearestNeighboursFrame(experiment, parent, digits);
        }
    }

    private static class AutomatedRandomForestsExperimentFrameSupplier
            implements ExperimentFrameSupplier<AutomatedRandomForests, AutomatedRandomForestsFrame> {

        @Override
        public AutomatedRandomForestsFrame get(AutomatedRandomForests experiment, JFrame parent, int digits) {
            return new AutomatedRandomForestsFrame("", experiment, parent, digits);
        }
    }

    private static class AutomatedHecExperimentFrameSupplier
            implements ExperimentFrameSupplier<AutomatedHeterogeneousEnsemble, AutomatedHeterogeneousEnsembleFrame> {

        @Override
        public AutomatedHeterogeneousEnsembleFrame get(AutomatedHeterogeneousEnsemble experiment, JFrame parent,
                                                       int digits) {
            return new AutomatedHeterogeneousEnsembleFrame("", experiment, parent, digits);
        }
    }

    private static class AutomatedStackingExperimentFrameSupplier
            implements ExperimentFrameSupplier<AutomatedStacking, AutomatedStackingFrame> {

        @Override
        public AutomatedStackingFrame get(AutomatedStacking experiment, JFrame parent, int digits) {
            return new AutomatedStackingFrame("", experiment, parent, digits);
        }
    }
}
