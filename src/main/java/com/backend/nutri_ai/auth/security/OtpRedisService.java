package com.backend.nutri_ai.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate; // 1. Import cái này
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpRedisService {

    // 2. Đổi RedisTemplate<String, Object> thành StringRedisTemplate
    private final StringRedisTemplate redisTemplate;

    private static final long OTP_EXPIRE_MINUTES = 5;

    public void saveOtp(String key, String otp) {
        // Method opsForValue() vẫn hoạt động bình thường
        redisTemplate.opsForValue()
                .set(key, otp, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public String getOtp(String key) {
        // StringRedisTemplate luôn trả về String, không cần ép kiểu Object nữa
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteOtp(String key) {
        redisTemplate.delete(key);
    }
}