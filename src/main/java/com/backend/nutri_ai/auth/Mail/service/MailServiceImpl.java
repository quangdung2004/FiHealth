package com.backend.nutri_ai.auth.Mail.service;


import com.backend.nutri_ai.auth.util.MailTemplateUtil;
import com.backend.nutri_ai.auth.constant.MailConstant;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpMail(String to, String otp) {
        sendHtml(
                to,
                MailConstant.SUBJECT_OTP,
                MailTemplateUtil.otpTemplate(otp)
        );
    }

    @Override
    public void sendResetPasswordMail(String to, String otp) {
        sendHtml(
                to,
                MailConstant.SUBJECT_RESET_PASSWORD,
                MailTemplateUtil.resetPasswordTemplate(otp)
        );
    }

    @Override
    public void sendActivationMail(String to, String activationLink) {
        sendHtml(
                to,
                MailConstant.SUBJECT_ACTIVATE_ACCOUNT,
                MailTemplateUtil.activationTemplate(activationLink)
        );
    }

    @Override
    public void sendNewPasswordMail(String to, String newPassword) {
        String subject = "Cấp lại mật khẩu mới - FiHealth";

        // Tạo nội dung HTML đẹp mắt một chút
        String htmlContent =
                "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd;'>" +
                        "<h2 style='color: #2c3e50;'>Mật khẩu mới của bạn</h2>" +
                        "<p>Xin chào,</p>" +
                        "<p>Hệ thống đã nhận được yêu cầu cấp lại mật khẩu của bạn.</p>" +
                        "<p>Đây là mật khẩu mới ngẫu nhiên của bạn:</p>" +
                        "<h3 style='color: #d35400; letter-spacing: 2px;'>" + newPassword + "</h3>" +
                        "<p>Vui lòng sử dụng mật khẩu này để đăng nhập và <b>đổi lại mật khẩu khác</b> ngay lập tức để bảo mật.</p>" +
                        "<p>Trân trọng,<br>Đội ngũ FiHealth</p>" +
                        "</div>";

        sendHtml(to, subject, htmlContent);
    }
    @Override
    public void sendHtmlMail(String to, String subject, String html) {
        sendHtml(to, subject, html);
    }
    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(msg, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Send mail failed", e);
        }
    }
    @Override
    public void sendPremiumThankYouMail(String to, String fullName, int durationDays, Long amount, String orderCode) {
        String subject = "Cảm ơn bạn đã đăng ký FiHealth Premium";
        String html = MailTemplateUtil.premiumThankYouTemplate(fullName, durationDays, amount, orderCode);
        sendHtml(to, subject, html);
    }


}
