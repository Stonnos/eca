package eca.dataminer;

import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import eca.metrics.distances.DistanceType;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal number of neighbours
 * for knn algorithm.
 *
 * @author Roman Batygin
 */
public class KNearestNeighboursOptimizer extends AbstractExperiment<KNearestNeighbours> {

    /**
     * Available distance types.
     */
    private static final DistanceType[] DISTANCE_TYPES = DistanceType.values();

    private final DistanceBuilder distanceBuilder = new DistanceBuilder();

    /**
     * Creates <tt>KNearestNeighboursOptimizer</tt> object
     * @param data {@link Instances} object
     * @param kNearestNeighbours {@link KNearestNeighbours} object
     */
    public KNearestNeighboursOptimizer(Instances data, KNearestNeighbours kNearestNeighbours) {
        super(data, kNearestNeighbours);
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new KNearestNeighboursIterativeOptimizer();
    }

    /**
     * K - nearest neighbourIndex iterative optimizer.
     */
    private class KNearestNeighboursIterativeOptimizer implements IterativeExperiment {

        int index;
        int distanceIndex;
        int neighbourIndex;

        KNearestNeighboursIterativeOptimizer() {
            clearHistory();
        }

        @Override
        public EvaluationResults next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (distanceIndex == DISTANCE_TYPES.length) {
                neighbourIndex = 0;
            }

            EvaluationResults evaluationResults = null;

            for (neighbourIndex++; neighbourIndex <= getData().numInstances();) {
                KNearestNeighbours model = (KNearestNeighbours) AbstractClassifier.makeCopy(classifier);
                model.setNumNeighbours(neighbourIndex);
                model.setDistance(DISTANCE_TYPES[distanceIndex].handle(distanceBuilder));

                Evaluation evaluation = new Evaluation(getData());

                evaluation.kCrossValidateModel(model, getData(), getData().numInstances(), 1, r);

                evaluationResults = new EvaluationResults(model, evaluation);
            }

            if (neighbourIndex == getData().numInstances()) {
                distanceIndex++;
            }

            ++index;
            return evaluationResults;
        }

        @Override
        public boolean hasNext() {
            return index < getNumIts();
        }

        @Override
        public int getPercent() {
            return index * 100 / getNumIts();
        }

        private int getNumIts() {
            return getData().numInstances() * DISTANCE_TYPES.length;
        }
    }

}
