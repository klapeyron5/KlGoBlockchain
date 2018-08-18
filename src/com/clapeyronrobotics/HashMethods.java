package com.clapeyronrobotics;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashMethods {

    public static byte[] StringToBytesUTF8(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }

    public static String GetSha256(byte[] data) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
            byte[] result = mDigest.digest(data);
            return ByteArrayToHexString(result);
        } catch (NoSuchAlgorithmException e) {
            return "NO_HASH";
        }
    }

    public static String ByteArrayToHexString(byte[] array) {
        String answerString = "";
        for(int i = 0; i < array.length; i++)
            answerString += String.format("%2s", Integer.toHexString(array[i] & 0xFF)).replace(' ', '0');
        return answerString;
    }
}
