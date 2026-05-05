package com.ksga.eventhub.service;


public interface OtpService {
    String generateOtp();

    void sendOtp(String email, String OTP, long ttl);

    boolean verifyOtp(String email, String OTP);

    boolean isOtpPresent(String email);

    String generateResetToken(String email);

    String validateResetToken(String resetToken);
}
