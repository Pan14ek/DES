package ua.nure.makieiev.lab1.common.converter;

import java.util.Arrays;

import static ua.nure.makieiev.lab1.common.constants.DesConstants.EMPTY;

public class SymbolConverter {

    private static final int FIRST_SYMBOL = 1;
    private static final int BINARY_FORMAT = 0x100;

    public static int[] getBinaryArray(String line) {
        byte[] lineBytes = line.getBytes();
        String binaries = convertByteToBinary(lineBytes);
        String[] binaryStringArray = binaries.split(EMPTY);
        return Arrays.stream(binaryStringArray).mapToInt(Integer::parseInt).toArray();
    }

    private static String convertByteToBinary(byte[] lineBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte lineByte : lineBytes) {
            String binaryString = getBinaryString(lineByte);
            stringBuilder.append(binaryString);
        }
        return stringBuilder.toString();
    }

    private static String getBinaryString(byte lineByte) {
        return Integer.toBinaryString(BINARY_FORMAT + lineByte).substring(FIRST_SYMBOL);
    }

    public static String mergeBits(int[] bits) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int value : bits) {
            stringBuilder.append(value);
        }
        return stringBuilder.toString().trim();
    }

}