/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.ensemble.sampling.Sampler;
import eca.ensemble.voting.MajorityVoting;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Random;

/**
 * Implements heterogeneous ensemble algorithm. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Use weighted votes method. (Default: <tt>false</tt>) <p>
 * <p>
 * Use randomly classifiers selection. (Default: <tt>true</tt>) <p>
 * <p>
 * Set individual classifiers collection  <p>
 * <p>
 * Set minimum error threshold for including classifier in ensemble <p>
 * <p>
 * Set maximum error threshold for including classifier in ensemble <p>
 * <p>
 * Sets {@link Sampler} object. <p>
 *
 * @author Roman Batygin
 */
public class DaggingClassifier extends AbstractHeterogeneousClassifier {

    /**
     * Creates new Dagging classifier object.
     */
    public DaggingClassifier() {
    }

    /**
     * Creates Dagging classifier object with given classifiers set.
     *
     * @param classifiersSet classifiers set
     */
    public DaggingClassifier(ClassifiersSet classifiersSet) {
        super(classifiersSet);
    }

    @Override
    public String[] getOptions() {
        String[] options = new String[(getClassifiersSet().size() + 2) * 2];
        int k = 0;
        options[k++] = EnsembleDictionary.NUM_FOLDS;
        options[k++] = String.valueOf(getNumIterations());
        options[k++] = EnsembleDictionary.SEED;
        options[k++] = String.valueOf(getSeed());
        for (int j = 0; k < options.length; k += 2, j++) {
            options[k] = String.format(EnsembleDictionary.INDIVIDUAL_CLASSIFIER_FORMAT, j);
            options[k + 1] = getClassifiersSet().getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    @Override
    protected void initializeOptions() {
        if (getNumIterations() > 1) {
            filteredData.randomize(random);
            filteredData.stratify(getNumIterations());
        }
        votes = new MajorityVoting(new Aggregator(classifiers, filteredData));
    }

    @Override
    protected Instances createSample(int iteration) {
        return getNumIterations() > 1 ? filteredData.testCV(getNumIterations(), iteration) : filteredData;
    }

    @Override
    protected Classifier buildNextClassifier(int iteration, Instances data) throws Exception {
        return ClassifierBuilder.buildRandomClassifier(getClassifiersSet(), data, new Random(seeds[iteration]),
                seeds[iteration]);
    }

    @Override
    protected synchronized void addClassifier(int iteration, Classifier classifier, Instances data) {
        classifiers.add(new ClassifierOrderModel(classifier, iteration));
    }
}