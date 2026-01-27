package com.backend.nutri_ai.auth.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class generateRandomPassword {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARS = UPPER + LOWER + DIGITS;

    // Sử dụng SecureRandom thay vì Random thường để bảo mật hơn
    private static final SecureRandom RANDOM = new SecureRandom();

    // Private constructor để chặn việc khởi tạo class này
    private generateRandomPassword() {
    }


    /**
     * Sinh mật khẩu ngẫu nhiên độ dài 10 ký tự.
     * Bao gồm: Chữ hoa, Chữ thường, Số.
     * Đảm bảo: Ít nhất 1 hoa, 1 thường, 1 số.
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder(10);

        // 1. Đảm bảo có ít nhất 1 chữ in hoa
        sb.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));

        // 2. Đảm bảo có ít nhất 1 chữ thường
        sb.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));

        // 3. Đảm bảo có ít nhất 1 số
        sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));

        // 4. Điền nốt 7 ký tự còn lại ngẫu nhiên
        for (int i = 0; i < 7; i++) {
            sb.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }

        // 5. Trộn ngẫu nhiên vị trí các ký tự (Shuffle)
        return shuffleString(sb.toString());
    }

    // Hàm phụ trợ để trộn chuỗi
    private static String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters, RANDOM);

        StringBuilder output = new StringBuilder();
        for (char c : characters) {
            output.append(c);
        }
        return output.toString();
    }
}
