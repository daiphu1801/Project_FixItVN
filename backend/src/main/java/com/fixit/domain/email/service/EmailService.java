package com.fixit.domain.email.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otpCode);
}
