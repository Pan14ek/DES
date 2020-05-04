package ua.nure.makieiev.lab1.algorithm;

import org.junit.Assert;
import org.junit.Test;
import ua.nure.makieiev.lab1.common.converter.SymbolConverter;

public class DesTest {

    @Test
    public void shouldGetPositiveResult() {
        Des des = new Des();
        String expected = "Hello wo";
        int[] totalArrayBites = SymbolConverter.getBinaryArray(expected);
        int[] keyBites = SymbolConverter.getBinaryArray("AAAAAAAA");

        int[] encryptBits = des.encrypt(totalArrayBites, keyBites);
        int[] decryptBits = des.decrypt(encryptBits, keyBites);

        String actual = SymbolConverter.convertBinaryToText(SymbolConverter.mergeBits(decryptBits));

        Assert.assertEquals(expected, actual);
    }

}