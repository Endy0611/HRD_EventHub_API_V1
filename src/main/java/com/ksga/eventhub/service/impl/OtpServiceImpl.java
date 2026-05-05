package com.ksga.eventhub.service.impl;

import com.ksga.eventhub.exception.BadRequestException;
import com.ksga.eventhub.service.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
@Service
@Slf4j
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final long OTP_TTL_SECONDS = 300;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String mailFrom;

    private final Map<String, String>        otpStorage        = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> otpExpiry         = new ConcurrentHashMap<>();
    private final Map<String, String>        resetTokenStorage = new ConcurrentHashMap<>();

    @Override
    public String generateOtp() {
        int otp = 100000 + (int)(Math.random() * 900000);
        return String.valueOf(otp);
    }

    @Override
    public void sendOtp(String email, String otp) {
        otpStorage.put(email, otp);
        otpExpiry.put(email, LocalDateTime.now().plusSeconds(OTP_TTL_SECONDS));

        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", OTP_TTL_SECONDS / 60); // shows "5" in email

        String htmlContent = templateEngine.process("otp-email", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(email);
            helper.setSubject("Verify your email with OTP");
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Sent OTP to: {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send OTP to {}", email, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        LocalDateTime expiry = otpExpiry.get(email);

        if (storedOtp != null
                && expiry != null
                && LocalDateTime.now().isBefore(expiry)
                && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            return true;
        }
        return false;
    }

    @Override
    public boolean isOtpPresent(String email) {
        LocalDateTime expiry = otpExpiry.get(email);
        return otpStorage.containsKey(email)
                && expiry != null
                && LocalDateTime.now().isBefore(expiry);
    }

    @Override
    public String generateResetToken(String email) {
        String token = UUID.randomUUID().toString();
        resetTokenStorage.put(token, email);
        return token;
    }

    @Override
    public String validateResetToken(String token) {
        String email = resetTokenStorage.get(token);
        if (email == null) throw new BadRequestException("Invalid or expired reset token.");
        resetTokenStorage.remove(token);
        return email;
    }
}