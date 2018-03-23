package eca.dataminer;

import eca.core.evaluation.EvaluationResults;
import eca.generators.NumberGenerator;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import eca.metrics.distances.DistanceType;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options for knn algorithm.
 *
 * @author Roman Batygin
 */
public class AutomatedKNearestNeighbours extends AbstractExperiment<KNearestNeighbours> {

    /**
     * Available distance types.
     */
    private static final DistanceType[] DISTANCE_TYPES = DistanceType.values();

    private static final DistanceBuilder DISTANCE_BUILDER = new DistanceBuilder();

    /**
     * Creates <tt>AutomatedKNearestNeighbours</tt> object
     *
     * @param data               {@link Instances} object
     * @param kNearestNeighbours {@link KNearestNeighbours} object
     */
    public AutomatedKNearestNeighbours(Instances data, KNearestNeighbours kNearestNeighbours) {
        super(data, kNearestNeighbours);
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new KNearestNeighboursIterativeBuilder();
    }

    /**
     * K - nearest neighbours iterative builder.
     */
    private class KNearestNeighboursIterativeBuilder extends AbstractIterativeBuilder {

        @Override
        public EvaluationResults next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            incrementIndex();
            KNearestNeighbours kNearestNeighbours = (KNearestNeighbours) AbstractClassifier.makeCopy(getClassifier());
            int neighbours = getRandom().nextInt(getData().numInstances() - 1) + 1;
            kNearestNeighbours.setNumNeighbours(neighbours);
            DistanceType distanceType = DISTANCE_TYPES[getRandom().nextInt(DISTANCE_TYPES.length)];
            kNearestNeighbours.setDistance(distanceType.handle(DISTANCE_BUILDER));
            kNearestNeighbours.setWeight(NumberGenerator.random(KNearestNeighbours.MIN_WEIGHT,
                    KNearestNeighbours.MAX_WEIGHT));
            return evaluateModel(kNearestNeighbours);
        }
    }

}
