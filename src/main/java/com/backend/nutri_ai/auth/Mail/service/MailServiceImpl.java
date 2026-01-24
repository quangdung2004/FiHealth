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

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Send mail failed", e);
        }
    }
}
