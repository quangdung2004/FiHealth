package com.backend.nutri_ai.auth.Mail.service;


public interface MailService {

    void sendOtpMail(String to, String otp);

    void sendResetPasswordMail(String to, String otp);

    void sendActivationMail(String to, String activationLink);
    public void sendNewPasswordMail(String to, String newPassword);
}
