package com.backend.nutri_ai.auth.util;


public class MailTemplateUtil {

    public static String otpTemplate(String otp) {
        return """
        <div style="font-family:Arial;max-width:600px;margin:auto">
            <h2>Xác thực tài khoản</h2>
            <p>Mã OTP của bạn:</p>
            <h1 style="color:#e74c3c">%s</h1>
            <p>Mã có hiệu lực trong 5 phút.</p>
        </div>
        """.formatted(otp);
    }

    public static String resetPasswordTemplate(String otp) {
        return """
        <div style="font-family:Arial">
            <h2>Reset mật khẩu</h2>
            <p>OTP của bạn:</p>
            <h1>%s</h1>
        </div>
        """.formatted(otp);
    }

    public static String activationTemplate(String link) {
        return """
        <div style="font-family:Arial">
            <h2>Kích hoạt tài khoản</h2>
            <p>Click link bên dưới:</p>
            <a href="%s">%s</a>
        </div>
        """.formatted(link, link);
    }
}
