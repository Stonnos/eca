package eca.client.adapter;

import eca.client.dto.options.AbstractHeterogeneousClassifierOptions;
import eca.client.dto.options.ClassifierOptions;
import eca.client.dto.options.StackingOptions;
import eca.client.mapping.AbstractClassifierMapper;
import eca.client.mapping.AdaBoostMapperImpl;
import eca.client.mapping.DecisionTreeMapperImpl;
import eca.client.mapping.ExtraTreesMapperImpl;
import eca.client.mapping.HeterogeneousClassifierMapperImpl;
import eca.client.mapping.J48MapperImpl;
import eca.client.mapping.KNearestNeighboursMapperImpl;
import eca.client.mapping.LogisticMapperImpl;
import eca.client.mapping.NeuralNetworkMapperImpl;
import eca.client.mapping.RandomForestsMapperImpl;
import eca.client.mapping.RandomNetworksMapperImpl;
import eca.client.mapping.StackingClassifierMapperImpl;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.StackingClassifier;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.AbstractClassifier;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Implements classifier options adapter class.
 *
 * @author Roman Batygin
 */
@Slf4j
@SuppressWarnings("unchecked")
public class ClassifierOptionsAdapter {

    private final List<AbstractClassifierMapper> classifierMappers = newArrayList();

    /**
     * Default constructor.
     */
    public ClassifierOptionsAdapter() {
        classifierMappers.add(new DecisionTreeMapperImpl());
        classifierMappers.add(new LogisticMapperImpl());
        classifierMappers.add(new KNearestNeighboursMapperImpl());
        classifierMappers.add(new NeuralNetworkMapperImpl());
        classifierMappers.add(new J48MapperImpl());
        classifierMappers.add(new StackingClassifierMapperImpl());
        classifierMappers.add(new RandomNetworksMapperImpl());
        classifierMappers.add(new ExtraTreesMapperImpl());
        classifierMappers.add(new RandomForestsMapperImpl());
        classifierMappers.add(new AdaBoostMapperImpl());
        classifierMappers.add(new HeterogeneousClassifierMapperImpl());
    }

    /**
     * Converts classifier model to its input options model.
     *
     * @param classifier - classifier object
     * @return classifiers options model
     */
    public ClassifierOptions convert(AbstractClassifier classifier) {
        AbstractClassifierMapper classifierMapper = classifierMappers.stream()
                .filter(mapper -> mapper.canMap(classifier))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can not convert '%s' classifier!", classifier.getClass().getSimpleName())));
        ClassifierOptions classifierOptions = classifierMapper.map(classifier);
        populateEnsembleClassifierOptions(classifier, classifierOptions);
        return classifierOptions;
    }

    private List<ClassifierOptions> convertClassifiersSet(ClassifiersSet classifiers) {
        List<ClassifierOptions> classifierOptions = new ArrayList<>(classifiers.size());
        classifiers.forEach(classifier -> classifierOptions.add(convert((AbstractClassifier) classifier)));
        return classifierOptions;
    }

    private void populateEnsembleClassifierOptions(AbstractClassifier classifier, ClassifierOptions classifierOptions) {
        if (EnsembleUtils.isHeterogeneousEnsembleClassifier(classifier)) {
            if (classifier instanceof AbstractHeterogeneousClassifier) {
                AbstractHeterogeneousClassifier heterogeneousClassifier =
                        (AbstractHeterogeneousClassifier) classifier;
                AbstractHeterogeneousClassifierOptions heterogeneousClassifierOptions =
                        (AbstractHeterogeneousClassifierOptions) classifierOptions;
                heterogeneousClassifierOptions.setClassifierOptions(
                        convertClassifiersSet(heterogeneousClassifier.getClassifiersSet()));
            } else if (classifier instanceof StackingClassifier) {
                StackingOptions stackingOptions = (StackingOptions) classifierOptions;
                StackingClassifier stackingClassifier = (StackingClassifier) classifier;
                stackingOptions.setMetaClassifierOptions(
                        convert((AbstractClassifier) stackingClassifier.getMetaClassifier()));
                stackingOptions.setClassifierOptions(
                        convertClassifiersSet(stackingClassifier.getClassifiers()));
            }
        }
    }
}
