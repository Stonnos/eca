package eca.dataminer;

import eca.util.MathUtils;

/**
 * Data miner utils class.
 *
 * @author Roman Batygin
 */
public class ExperimentUtil {

    /**
     * Returns the number of individual models combination.
     * The number of combinations is <code>P = sum[t = 1..r]r!/(t!(r-t)!)</code>
     *
     * @param r the number of individual models combination
     * @return the number of individual models combination
     */
    public static int getNumClassifiersCombinations(int r) {
        int fact = MathUtils.fact(r);
        int p = 0;
        for (int t = 1; t <= r; t++) {
            p += fact / (MathUtils.fact(t) * MathUtils.fact(r - t));
        }
        return p;
    }
}
