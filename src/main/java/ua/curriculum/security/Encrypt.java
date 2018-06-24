package ua.curriculum.security;

import org.apache.commons.codec.digest.DigestUtils;

public class Encrypt {
    public static String encryptString(String st) {
        String md5Hex = DigestUtils.md5Hex(st);

        return md5Hex;
    }
}
