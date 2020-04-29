package ua.nure.makieiev.lab1.algorithm;

import ua.nure.makieiev.lab1.common.constants.DesConstants;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Entropy {

    private static final int ARRAY_LENGTH = 8;

    private final int[][] roundArrays;

    public Entropy(int[][] roundArrays) {
        this.roundArrays = roundArrays;
    }

    public double[][] getEntropyResults() {
        double[][] entropyResults = new double[17][ARRAY_LENGTH];
        for (int i = 0; i < roundArrays.length; i++) {
            int[][] twoDimensionalArray = splitArrayToTwoDimensionalArray(roundArrays[i]);
            entropyResults[i] = calculateEntropy(twoDimensionalArray);
        }
        return entropyResults;
    }

    public double[] calculateEntropy(int[][] twoDimensionalArray) {
        double[] result = new double[ARRAY_LENGTH];
        AtomicInteger column = new AtomicInteger();
        IntStream.range(0, twoDimensionalArray.length)
                .forEach(i -> {
                    double p = (double) countValue(twoDimensionalArray, 0, column.getAndIncrement()) / ARRAY_LENGTH;
                    double q = 1 - p;
                    result[i] = entropyFormula(p, q);
                });
        return result;
    }

    private double entropyFormula(double p, double q) {
        double result;
        if (q == 0) {
            result = -1 * ((p * log2(p)));
        } else {
            result = -1 * ((p * log2(p)) + (q * log2(q)));
        }
        return result;
    }

    private double log2(double number) {
        return Math.log(number) / Math.log(2);
    }

    private long countValue(int[][] twoDimensionalArray, int value, int column) {
        return IntStream.range(0, twoDimensionalArray.length)
                .filter(i -> value == twoDimensionalArray[i][column])
                .count();
    }

    public int[][] splitArrayToTwoDimensionalArray(int[] array) {
        int[][] twoDimensionalArray = new int[ARRAY_LENGTH][ARRAY_LENGTH];
        IntStream.range(DesConstants.ZERO, ARRAY_LENGTH)
                .forEach(i -> twoDimensionalArray[i] = obtainNewArray(array, i));
        return twoDimensionalArray;
    }

    private int[] obtainNewArray(int[] array, int index) {
        int[] newArray = new int[ARRAY_LENGTH];
        AtomicInteger k = new AtomicInteger();
        int start = index * ARRAY_LENGTH;
        int end = ARRAY_LENGTH + start;
        IntStream.range(start, end)
                .forEach(i -> newArray[k.getAndIncrement()] = array[i]);
        return newArray;
    }

}