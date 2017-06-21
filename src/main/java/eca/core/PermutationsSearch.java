package eca.core;

/**
 * @author Roman Batygin
 */

public class PermutationsSearch {

    private int[] values;

    private boolean initial;

    public void setValues(int[] values) {
        this.values = values;
        this.initial = true;
    }

    public boolean nextPermutation() {
        if (initial) {
            initial = false;
            return true;
        }
        else return Permutation.nextPermutation(values);
    }

}
