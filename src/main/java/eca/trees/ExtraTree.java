package eca.trees;

import weka.core.Attribute;

import java.util.Enumeration;

/**
 * Class for generating extra tree model.
 *
 * @author Roman Batygin
 */
public class ExtraTree extends CART {

    @Override
    protected final SplitDescriptor createOptSplit(TreeNode x) {
        SplitDescriptor split = new SplitDescriptor(x, Double.MAX_VALUE);
        int k = numRandomAttr() == 0 ? getData().numAttributes() - 1 : numRandomAttr();

        for (Enumeration<Attribute> e = attributes(); e.hasMoreElements(); ) {
            processRandomSplit(e.nextElement(), splitAlgorithm, split, k);
        }

        return split;
    }
}
