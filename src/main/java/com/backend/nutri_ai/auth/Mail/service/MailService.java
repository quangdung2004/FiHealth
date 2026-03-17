package com.backend.nutri_ai.auth.Mail.service;


public interface MailService {

    void sendOtpMail(String to, String otp);

    void sendResetPasswordMail(String to, String otp);

    void sendActivationMail(String to, String activationLink);
    public void sendNewPasswordMail(String to, String newPassword);
    void sendHtmlMail(String to, String subject, String html);
    void sendPremiumThankYouMail(String to, String fullName, int durationDays, Long amount, String orderCode);
}
