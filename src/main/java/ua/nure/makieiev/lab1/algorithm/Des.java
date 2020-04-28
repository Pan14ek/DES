package ua.nure.makieiev.lab1.algorithm;

import ua.nure.makieiev.lab1.common.converter.SymbolConverter;
import ua.nure.makieiev.lab1.common.storage.ValueStorage;

import java.util.stream.IntStream;

import static ua.nure.makieiev.lab1.common.constants.DesConstants.EMPTY;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.FIFTY_SIX_SIZE;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.FOUR_BIT;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.HALF_PART_ARRAY;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.KEY_LENGTH;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.KEY_LENGTH_AFTER_CHANGE;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.ONE;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.RADIX_TWO;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.SIXTEEN_LOOPS;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.TOTAL_ARRAY_SIZE;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.TWENTY_EIGHT_SIZE;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.ZERO;
import static ua.nure.makieiev.lab1.common.constants.DesConstants.ZERO_BIT;

public class Des {

    public int[] encrypt(int[] textBits, int[] keysBits) {
        int[][] keys = prepareKeys(keysBits);
        textBits = permute(ValueStorage.IP, textBits);
        for (int i = ZERO; i < SIXTEEN_LOOPS; i++) {
            textBits = round(textBits, keys[i], i);
        }
        return permute(ValueStorage.FP, textBits);
    }

    public int[] decrypt(int[] textBits, int[] keysBits) {
        int[][] keys = prepareKeys(keysBits);
        textBits = permute(ValueStorage.IP, textBits);
        for (int i = 15; i >= ZERO; i--) {
            textBits = round(textBits, keys[i], 15 - i);
        }
        return permute(ValueStorage.FP, textBits);
    }

    private int[][] prepareKeys(int[] keyBits) {
        int[] cArray = obtainCArray(keyBits);
        int[] dArray = obtainDArray(keyBits);
        int[][] keys = new int[SIXTEEN_LOOPS][KEY_LENGTH_AFTER_CHANGE];
        for (int i = ZERO; i < SIXTEEN_LOOPS; i++) {
            int rotationTimes = ValueStorage.shiftBits[i];
            cArray = shiftLeft(cArray, rotationTimes);
            dArray = shiftLeft(dArray, rotationTimes);
            int[] key = obtainKey(cArray, dArray);
            keys[i] = getNewKey(key);
        }
        return keys;
    }

    private int[] obtainKey(int[] cArray, int[] dArray) {
        int[] key = new int[KEY_LENGTH];
        System.arraycopy(cArray, ZERO, key, ZERO, TWENTY_EIGHT_SIZE);
        System.arraycopy(dArray, ZERO, key, TWENTY_EIGHT_SIZE, TWENTY_EIGHT_SIZE);
        return key;
    }

    private int[] getNewKey(int[] key) {
        int[] newKey = new int[KEY_LENGTH_AFTER_CHANGE];
        IntStream.range(ZERO, newKey.length)
                .parallel()
                .forEach(j -> newKey[j] = key[ValueStorage.PC2[j] - ONE]);
        return newKey;
    }

    private int[] round(int[] array, int[] key, int index) {
        int[] newArray = new int[TOTAL_ARRAY_SIZE];
        int[] leftPart = new int[HALF_PART_ARRAY];
        int[] rightPart = new int[HALF_PART_ARRAY];
        int[] oldRightPart;
        System.arraycopy(array, ZERO, leftPart, ZERO, HALF_PART_ARRAY);
        System.arraycopy(array, HALF_PART_ARRAY, rightPart, ZERO, HALF_PART_ARRAY);
        oldRightPart = rightPart;
        rightPart = permute(ValueStorage.E, rightPart);
        rightPart = xor(rightPart, key);
        rightPart = sBlock(rightPart);
        leftPart = xor(leftPart, rightPart);
        System.arraycopy(leftPart, ZERO, newArray, ZERO, HALF_PART_ARRAY);
        System.arraycopy(oldRightPart, ZERO, newArray, HALF_PART_ARRAY, HALF_PART_ARRAY);
        printRoundInformation(rightPart, leftPart, key, index);
        return newArray;
    }

    private int[] sBlock(int[] bits) {
        int[] result = new int[HALF_PART_ARRAY];
        IntStream.range(ZERO, 8).forEach(i -> {
            int[] row = obtainRow(bits, i);
            String sRow = sumRow(row);
            int[] column = obtainColumn(bits, i);
            String sColumn = sumColumn(column);
            int iRow = Integer.parseInt(sRow, RADIX_TWO);
            int iColumn = Integer.parseInt(sColumn, RADIX_TWO);
            int x = ValueStorage.S_Box[i][(iRow * 16) + iColumn];
            StringBuilder stringBuilder = insertBinaryStringBuilder(x);
            IntStream.range(ZERO, FOUR_BIT)
                    .parallel()
                    .forEach(j -> result[(i * FOUR_BIT) + j] = Integer.parseInt(stringBuilder.charAt(j) + EMPTY));
        });
        return getFinalResult(result);
    }

    private int[] permute(int[] sequence, int[] dataArray) {
        int[] result = new int[sequence.length];
        IntStream.range(ZERO, sequence.length)
                .parallel()
                .forEach(i -> result[i] = dataArray[sequence[i] - ONE]);
        return result;
    }

    private int[] getFinalResult(int[] output) {
        int[] finalResult = new int[HALF_PART_ARRAY];
        IntStream.range(ZERO, HALF_PART_ARRAY)
                .parallel()
                .forEach(i -> finalResult[i] = output[ValueStorage.P[i] - ONE]);
        return finalResult;
    }

    private String sumRow(int[] row) {
        return String.join(EMPTY,
                String.valueOf(row[ZERO]),
                String.valueOf(row[ONE]));
    }

    private String sumColumn(int[] column) {
        return String.join(EMPTY,
                String.valueOf(column[ZERO]),
                String.valueOf(column[ONE]),
                String.valueOf(column[2]),
                String.valueOf(column[3]));
    }

    private int[] obtainRow(int[] bits, int index) {
        int[] row = new int[2];
        row[ZERO] = bits[6 * index];
        row[ONE] = bits[(6 * index) + 5];
        return row;
    }

    private int[] obtainColumn(int[] bits, int index) {
        int[] column = new int[FOUR_BIT];
        IntStream.range(ZERO, column.length)
                .parallel()
                .forEach(j -> column[j] = bits[(6 * index) + (j + ONE)]);
        return column;
    }

    private StringBuilder insertBinaryStringBuilder(int x) {
        StringBuilder stringBuilder = new StringBuilder(Integer.toBinaryString(x));
        while (stringBuilder.length() < FOUR_BIT) {
            stringBuilder.insert(ZERO, ZERO_BIT);
        }
        return stringBuilder;
    }

    private int[] shiftLeft(int[] bits, int rotationNumber) {
        int[] result = new int[bits.length];
        System.arraycopy(bits, ZERO, result, ZERO, bits.length);
        IntStream.range(ZERO, rotationNumber).forEach(i -> {
            int temp = result[ZERO];
            System.arraycopy(result, ONE, result, ZERO, bits.length - ONE);
            result[bits.length - ONE] = temp;
        });
        return result;
    }

    private int[] xor(int[] firstNumber, int[] secondNumber) {
        int[] result = new int[firstNumber.length];
        IntStream.range(ZERO, firstNumber.length)
                .parallel()
                .forEach(i -> result[i] = firstNumber[i] ^ secondNumber[i]);
        return result;
    }

    private int[] obtainCArray(int[] keyBits) {
        int[] cArray = new int[TWENTY_EIGHT_SIZE];
        for (int i = ZERO; i < TWENTY_EIGHT_SIZE; i++) {
            cArray[i] = keyBits[ValueStorage.PC1[i] - ONE];
        }
        return cArray;
    }

    private int[] obtainDArray(int[] keyBits) {
        int[] dArray = new int[TWENTY_EIGHT_SIZE];
        for (int i = TWENTY_EIGHT_SIZE; i < FIFTY_SIX_SIZE; i++) {
            dArray[i - TWENTY_EIGHT_SIZE] = keyBits[ValueStorage.PC1[i] - ONE];
        }
        return dArray;
    }

    private void printRoundInformation(int[] rightPart, int[] leftPart, int[] key, int index) {
        System.out.println("Round " + (index + ONE) +
                "| Right = " + SymbolConverter.mergeBits(rightPart) +
                " | Left = " + SymbolConverter.mergeBits(leftPart) +
                " | Key = " + SymbolConverter.mergeBits(key));
    }

}