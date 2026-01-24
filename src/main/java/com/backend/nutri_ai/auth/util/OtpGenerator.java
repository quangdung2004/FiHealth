package com.backend.nutri_ai.auth.util;


import java.security.SecureRandom;

public final class OtpGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int DEFAULT_LENGTH = 6;

    private OtpGenerator() {}


    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }


    public static String generate(int length) {
        if (length < 4 || length > 8) {
            throw new IllegalArgumentException("OTP length must be between 4 and 8");
        }

        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;

        int otp = secureRandom.nextInt(max - min + 1) + min;
        return String.valueOf(otp);
    }
}
