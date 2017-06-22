/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import weka.core.Instances;
import weka.classifiers.Classifier;
import java.util.NoSuchElementException;

/**
 *
 * @author Рома
 */
public class HeterogeneousClassifier extends AbstractHeterogeneousClassifier
        implements Voteable {

    private boolean use_Weighted_Votes;
    private boolean use_Random_Classifier = true;
    private final Sampler sampler = new Sampler();

    public HeterogeneousClassifier(ClassifiersSet set) {
        super(set);
    }

    public final Sampler sampler() {
        return sampler;
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        if (sampler.getSampling() == Sampler.INITIAL) {
            numIterations = set.size();
        }
        votes = (getUseWeightedVotesMethod())
                ? new WeightedVoting(new Aggregator(this), numIterations)
                : new MajorityVoting(new Aggregator(this));
        return new HeterogeneousBuilder(data);
    }

    @Override
    public boolean getUseWeightedVotesMethod() {
        return use_Weighted_Votes;
    }

    @Override
    public void setUseWeightedVotesMethod(boolean flag) {
        this.use_Weighted_Votes = flag;
    }

    public boolean getUseRandomClassifier() {
        return use_Random_Classifier;
    }

    public void setUseRandomClassifier(boolean flag) {
        this.use_Random_Classifier = flag;
    }

    @Override
    public String[] getOptions() {
        String[] options = new String[(set.size() + 6) * 2];
        int k = 0;
        options[k++] = "Число итераций:";
        options[k++] = String.valueOf(numIterations);
        options[k++] = "Минимальная допустимая ошибка классификатора:";
        options[k++] = String.valueOf(min_error);
        options[k++] = "Максимальная допустимая ошибка классификатора:";
        options[k++] = String.valueOf(max_error);
        options[k++] = "Метод голосования:";
        options[k++] = votes.getDescription();
        options[k++] = "Формирование обучающих выборок:";
        options[k++] = sampler.getDescription();
        options[k++] = "Выбор классификатора:";
        options[k++] = use_Random_Classifier ? "Случайный классификатор"
                : "Оптимальный классификатор";
        for (int i = k++, j = 0; i < options.length; i += 2, j++) {
            options[i] = "Базовый классификатор " + j + ":";
            options[i + 1] = set.getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    /**
     *
     */
    private class HeterogeneousBuilder extends AbstractBuilder {

        public HeterogeneousBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Instances bag = sampler.instances(data);
            Classifier model;
            if (sampler.getSampling() == Sampler.INITIAL) {
                model = set.buildClassifier(index, bag);
            }
            else if (getUseRandomClassifier()) {
                model = set.buildRandomClassifier(bag);
            }
            else {
                model = set.builtOptimalClassifier(bag);
            }

            double error = Evaluation.error(model, bag);

            if (error > min_error && error < max_error) {
                classifiers.add(model);
                if (getUseWeightedVotesMethod()) {
                    ((WeightedVoting) votes).setWeight(0.5 * Math.log((1.0 - error) / error));
                }
            }
            if (index == numIterations - 1) {
                checkModel();
            }
            return ++index;
        }

    } //End of class HeterogeneousBuilder

} //End of class HeterogeneousClassifier
