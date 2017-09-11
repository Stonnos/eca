/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.statistics;

/**
 * Statistics class.
 * @author Roman93
 */
public class Statistics {

    /**
     * Calculates Student critical value.
     * @param df - the number of freedom degrees
     * @param p - p - value
     * @return the value of Student critical value
     */
    public static double studentCriticalValue(int df, double p) {
        return Math.sqrt(FCriticalValue(p, 1, df));
    }

    /**
     * Calculates the value of Student confidence interval.
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
     * @param p - p - value
     * @param df1
     * @param df2
     * @return the F - critical value
     */
    public static double FCriticalValue(double p, int df1, int df2) {
        double maxf = 99999.0;
        double minf = .000001;
        if (p <= 0.0 || p >= 1.0) {
            return 0.0;
        }
        double fval = 1.0 / p;
        while (Math.abs(maxf - minf) > .000001) {
            if (weka.core.Statistics.FProbability(fval, df1, df2) < p) {
                maxf = fval;
            } else {
                minf = fval;
            }
            fval = (maxf + minf) * 0.5;
        }
        return fval;
    }

    /**
     * Calculates the hi - square critical value.
     * @param p - p - value
     * @param df - the number of freedom degrees
     * @return the hi - square critical value
     */
    public static double chiSquaredCriticalValue(double p, int df) {
        double maxf = 99999.0;
        double minf = .000001;
        if (p <= 0.0 || p >= 1.0) {
            return 0.0;
        }
        double fval = 1.0 / p;
        while (Math.abs(maxf - minf) > .000001) {
            if (weka.core.Statistics.chiSquaredProbability(fval, df) < p) {
                maxf = fval;
            } else {
                minf = fval;
            }
            fval = (maxf + minf) * 0.5;
        }
        return fval;
    }

}
