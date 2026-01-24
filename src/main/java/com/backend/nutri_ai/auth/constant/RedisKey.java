package com.backend.nutri_ai.auth.constant;

public class RedisKey {

    private RedisKey() {}

    // OTP đăng ký tài khoản
    public static final String REGISTER_OTP = "auth:otp:register:";

    // OTP quên mật khẩu
    public static final String RESET_PASSWORD_OTP = "auth:otp:reset:";

    // Giới hạn gửi OTP (chống spam)
    public static final String OTP_RATE_LIMIT = "auth:otp:limit:";
}

