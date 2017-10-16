package eca.core;

/**
 * Class for searching all permutations with repetitions in array.
 *
 * @author Roman Batygin
 */
public class PermutationsSearcher {

    /**
     * Input array
     **/
    private int[] values;

    /**
     * Is first permutation?
     **/
    private boolean first;

    /**
     * Sets input array ordering by decrease.
     *
     * @param values input array ordering by decrease
     */
    public void setValues(int[] values) {
        this.values = values;
        this.first = true;
    }

    /**
     * Returns the next permutation in array.
     *
     * @return <tt>true</tt> if the next permutation is exist
     */
    public boolean nextPermutation() {
        if (first) {
            first = false;
            return true;
        } else {
            return Permutation.nextPermutation(values);
        }
    }

}
