package eca.experiment;

import eca.core.Utils;

/**
 * @author Roman Batygin
 */

public class ExperimentUtil {

    public static int getNumClassifiersCombinations(int r) {
        int fact = Utils.fact(r);
        int p = 0;
        for (int t = 1; t <= r; t++) {
            p += fact / (Utils.fact(t) * Utils.fact(r - t));
        }
        return p;
    }
}
