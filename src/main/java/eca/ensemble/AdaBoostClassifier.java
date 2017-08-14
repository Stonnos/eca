/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Implements AdaBoost algorithm. For more information see <p>
 * Yoav Freund, Robert E. Schapire. A decision-theoretic generalization of online learning
 * and an application to boosting // Second European Conference on Computational Learning Theory. – 1995. <p>
 *
 * @author Рома
 */
public class AdaBoostClassifier extends AbstractHeterogeneousClassifier {

    /**
     * Instances weights
     **/
    private double[] weights;

    private final Random random = new Random();

    /**
     * Creates <tt>AdaBoostClassifier</tt> object with given classifiers set.
     *
     * @param set classifiers set
     */
    public AdaBoostClassifier(ClassifiersSet set) {
        super(set);
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new AdaBoostBuilder(data);
    }

    @Override
    public String[] getOptions() {
        String[] options = new String[(set.size() + 3) * 2];
        int k = 0;
        options[k++] = "Число итераций:";
        options[k++] = String.valueOf(numIterations);
        options[k++] = "Минимальная допустимая ошибка классификатора:";
        options[k++] = String.valueOf(min_error);
        options[k++] = "Максимальная допустимая ошибка классификатора:";
        options[k++] = String.valueOf(max_error);
        for (int i = k++, j = 0; i < options.length; i += 2, j++) {
            options[i] = "Базовый классификатор " + j + ":";
            options[i + 1] = set.getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    private double weightedError(Classifier model) throws Exception {
        double error = 0.0;
        for (int i = 0; i < filteredData.numInstances(); i++) {
            Instance obj = filteredData.instance(i);
            if (obj.classValue() != model.classifyInstance(obj)) {
                error += weights[i];
            }
        }
        return error;
    }

    private void initializeWeights() {
        double w0 = 1.0 / filteredData.numInstances();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = w0;
        }
    }

    private void updateWeights(int t) throws Exception {
        double sumWeights = 0.0;
        WeightedVoting v = (WeightedVoting) votes;
        for (int i = 0; i < weights.length; i++) {
            Instance obj = filteredData.instance(i);
            int sign = obj.classValue() == classifiers.get(t).classifyInstance(obj) ? 1 : -1;
            weights[i] = weights[i] * Math.exp(-v.getWeight(t) * sign);
            sumWeights += weights[i];
        }
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / sumWeights;
        }
    }

    private boolean nextIteration(int t) throws Exception {
        Instances sample = filteredData.resampleWithWeights(random, weights);
        Classifier model = null;
        double minError = Double.MAX_VALUE;
        for (int i = 0; i < set.size(); i++) {
            Classifier c = set.getClassifierCopy(i);
            c.buildClassifier(sample);
            double error = weightedError(c);
            if (error < minError) {
                minError = error;
                model = c;
            }
        }
        if (minError > min_error && minError < max_error) {
            classifiers.add(model);
            ((WeightedVoting) votes).setWeight(0.5 * Math.log((1.0 - minError) / minError));
            updateWeights(t);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void initialize() {
        votes = new WeightedVoting(new Aggregator(this), numIterations);
        weights = new double[filteredData.numInstances()];
        initializeWeights();
    }

    /**
     *
     */
    private class AdaBoostBuilder extends AbstractBuilder {

        public AdaBoostBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (!nextIteration(index)) {
                step = numIterations - index;
                index = numIterations - 1;
            }
            if (index == numIterations - 1) {
                checkModel();
            }
            return ++index;
        }

    } //End of class AdaBoostBuilder

} //End of class AdaBoostClassifier
