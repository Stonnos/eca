package eca.core;

/**
 * Class for searching all permutations with repetitions in array.
 * @author Roman Batygin
 */
public class PermutationsSearch {

    /** Input array **/
    private int[] values;

    /** Is first permutation? **/
    private boolean initial;

    /**
     * Sets input array order by decrease.
     * @param values input array order by decrease
     */
    public void setValues(int[] values) {
        this.values = values;
        this.initial = true;
    }

    /**
     * Returns the next permutation in array.
     * @return <tt>true</tt> if the next permutation is exist
     */
    public boolean nextPermutation() {
        if (initial) {
            initial = false;
            return true;
        }
        else return Permutation.nextPermutation(values);
    }

}
