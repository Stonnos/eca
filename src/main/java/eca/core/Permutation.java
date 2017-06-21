/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

/**
 *
 * @author Рома
 */
public class Permutation {

    public static boolean nextPermutation(int[] a) {
        int j = a.length - 2;
        while (j != -1 && a[j] <= a[j + 1]) {
            j--;
        }
        if (j == -1) {
            return false;
        }
        int k = a.length - 1;
        while (a[j] <= a[k]) {
            k--;
        }
        swap(a, j, k);
        int l = j + 1, r = a.length - 1;
        while (l < r) {
            swap(a, l++, r--);
        }
        return true;
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

}
