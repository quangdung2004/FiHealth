package com.backend.nutri_ai.auth.constant;


public class MailConstant {

    private static final String BRAND_PREFIX = "[NutriAI] ";
    public static final String SUBJECT_OTP = BRAND_PREFIX + "Mã xác thực tài khoản của bạn";
    public static final String SUBJECT_RESET_PASSWORD = BRAND_PREFIX + "Yêu cầu đặt lại mật khẩu";
    public static final String SUBJECT_ACTIVATE_ACCOUNT = BRAND_PREFIX + "Chào mừng! Hãy kích hoạt tài khoản ngay";
    public static final String SUBJECT_PASSWORD_CHANGED = BRAND_PREFIX + "Mật khẩu của bạn đã được thay đổi thành công";

    public static final int OTP_EXPIRE_MINUTES = 5;
}