package eca.ensemble;

import eca.trees.DecisionTreeClassifier;
import eca.trees.ExtraTree;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Class for generating Extra trees model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Set minimum number of instances per leaf. (Default: 2) <p>
 * <p>
 * Set maximum tree depth. (Default: 0 (denotes infinity)) <p>
 * <p>
 * Set number of random attributes at each split. (Default: 0 (denotes all attributes)) <p>
 * <p>
 * Set use bootstrap sample at each iteration. (Default: <tt>false</tt>) <p>
 *
 * @author Roman Batygin
 */
public class ExtraTreesClassifier extends RandomForests {

    private boolean useBootstrapSamples;

    public boolean isUseBootstrapSamples() {
        return useBootstrapSamples;
    }

    public void setUseBootstrapSamples(boolean useBootstrapSamples) {
        this.useBootstrapSamples = useBootstrapSamples;
    }

    private class ExtraTreesBuilder extends ForestBuilder {

        public ExtraTreesBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Instances sample;
            if (isUseBootstrapSamples()) {
                sample = sampler.bootstrap(filteredData);
            } else {
                sample = sampler.initial(filteredData);
            }

            DecisionTreeClassifier treeClassifier = new ExtraTree();
            treeClassifier.setRandomTree(true);
            treeClassifier.setNumRandomAttr(getNumRandomAttr());
            treeClassifier.setMinObj(getMinObj());
            treeClassifier.setMaxDepth(getMaxDepth());
            treeClassifier.buildClassifier(sample);
            classifiers.add(treeClassifier);
            return ++index;
        }
    }
}
