/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.statistics;

import lombok.experimental.UtilityClass;

/**
 * Statistics class.
 */
@UtilityClass
public class Statistics {

    private static final double MAX_VALUE = 99999.0;
    private static final double MIN_VALUE = .000001;

    /**
     * Calculates Student critical value.
     *
     * @param df - the number of freedom degrees
     * @param p  - p - value
     * @return the value of Student critical value
     */
    public static double studentCriticalValue(int df, double p) {
        return Math.sqrt(fCriticalValue(p, 1, df));
    }

    /**
     * Calculates the value of Student confidence interval.
     *
     * @param n - sample size
     * @param p - p - value
     * @param s - std. dev. value
     * @return the value of Student confidence interval.
     */
    public static double studentConfidenceInterval(int n, double p, double s) {
        return s * studentCriticalValue(n - 1, p) / Math.sqrt(n);
    }

    /**
     * Calculates the F - critical value.
     *
     * @param p   - p - value
     * @param df1 - the number of freedom degrees greater variance
     * @param df2 - the number of freedom degrees less variance
     * @return the F - critical value
     */
    public static double fCriticalValue(double p, int df1, int df2) {
        double maxF = MAX_VALUE;
        double minF = MIN_VALUE;
        if (p <= 0.0 || p >= 1.0) {
            return 0.0;
        }
        double fVal = 1.0 / p;
        while (Math.abs(maxF - minF) > MIN_VALUE) {
            if (weka.core.Statistics.FProbability(fVal, df1, df2) < p) {
                maxF = fVal;
            } else {
                minF = fVal;
            }
            fVal = (maxF + minF) * 0.5;
        }
        return fVal;
    }

    /**
     * Calculates the hi - square critical value.
     *
     * @param p  - p - value
     * @param df - the number of freedom degrees
     * @return the hi - square critical value
     */
    public static double chiSquaredCriticalValue(double p, int df) {
        double maxF = MAX_VALUE;
        double minF = MIN_VALUE;
        if (p <= 0.0 || p >= 1.0) {
            return 0.0;
        }
        double chiVal = 1.0 / p;
        while (Math.abs(maxF - minF) > MIN_VALUE) {
            if (weka.core.Statistics.chiSquaredProbability(chiVal, df) < p) {
                maxF = chiVal;
            } else {
                minF = chiVal;
            }
            chiVal = (maxF + minF) * 0.5;
        }
        return chiVal;
    }

}
