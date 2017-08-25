/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.PermutationsSearch;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.StackingClassifier;
import eca.model.ClassifierDescriptor;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options
 * for stacking algorithm based on experiment series.
 *
 * @author Roman93
 */
public class AutomatedStacking extends AbstractExperiment<StackingClassifier> {

    /**
     * Creates <tt>AutomatedStacking</tt> object with given options.
     *
     * @param classifier classifier object
     * @param data       training data
     */
    public AutomatedStacking(StackingClassifier classifier, Instances data) {
        super(data, classifier);
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new AutomatedStackingBuilder();
    }

    /**
     *
     */
    private class AutomatedStackingBuilder implements IterativeExperiment {

        int index, state, it, metaClsIndex;
        ClassifiersSet set = classifier.getClassifiers();
        ClassifiersSet currentSet = new ClassifiersSet();
        int[] marks = new int[classifier.getClassifiers().size()];
        PermutationsSearch permutationsSearch = new PermutationsSearch();
        int numCombinations = getNumCombinations();

        AutomatedStackingBuilder() {
            clearHistory();
        }

        @Override
        public ClassifierDescriptor next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            ClassifierDescriptor object = null;

            while (object == null) {
                object = nextState();
            }
            return object;
        }

        @Override
        public boolean hasNext() {
            return index < numCombinations;
        }

        @Override
        public int getPercent() {
            return index * 100 / numCombinations;
        }

        ClassifierDescriptor nextState() throws Exception {

            switch (state) {

                case 0: {
                    Classifier metaClassifier = classifier.getClassifiers().getClassifierCopy(metaClsIndex);
                    classifier.setMetaClassifier(metaClassifier);
                    state = 1;
                    break;
                }

                case 1: {
                    if (it == marks.length) {
                        metaClsIndex++;
                        it = 0;
                        state = 0;
                    } else {
                        for (int j = 0; j < marks.length; j++) {
                            marks[j] = j <= it ? 1 : 0;
                        }
                        permutationsSearch.setValues(marks);
                        state = 2;
                    }
                    break;
                }

                case 2: {
                    if (permutationsSearch.nextPermutation()) {

                        currentSet.clear();
                        for (int k = 0; k < marks.length; k++) {
                            if (marks[k] == 1) {
                                currentSet.addClassifier(set.getClassifier(k));
                            }
                        }

                        state = 3;
                    } else {
                        it++;
                        state = 1;
                    }

                    break;
                }

                case 3: {
                    StackingClassifier model
                            = (StackingClassifier) AbstractClassifier.makeCopy(classifier);
                    model.setClassifiers(currentSet.clone());
                    ClassifierDescriptor classifierDescriptor = evaluateModel(model);
                    state = 2;
                    index++;
                    return classifierDescriptor;
                }

            }

            return null;
        }

        int getNumCombinations() {
            return set.size() * ExperimentUtil.getNumClassifiersCombinations(set.size());
        }

    }

}
