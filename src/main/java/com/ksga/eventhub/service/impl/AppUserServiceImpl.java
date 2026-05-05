package com.ksga.eventhub.service.impl;

import com.ksga.eventhub.exception.BadRequestException; // Ensure you have this custom exception
import com.ksga.eventhub.exception.DuplicateUserException; // Ensure you have this custom exception
import com.ksga.eventhub.model.dto.auth.request.AppUserRequest;
import com.ksga.eventhub.model.dto.auth.response.AppUserResponse;
import com.ksga.eventhub.model.entity.AppUser;
import com.ksga.eventhub.repository.AppUserRepository;
import com.ksga.eventhub.service.AppUserService;
import com.ksga.eventhub.service.OtpService; // Assuming you have an OtpService
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final OtpService otpService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
    }

    @Override
    public AppUserResponse register(AppUserRequest request) {

        if (appUserRepository.existsUserEmail(request.getEmail())) {
            throw new DuplicateUserException("Email already registered: " + request.getEmail());
        }

        if (appUserRepository.existsUserName(request.getUsername())) {
            throw new DuplicateUserException("Username already taken: " + request.getUsername());
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        AppUser appUser = appUserRepository.register(request);


        String otp = otpService.generateOtp();
        otpService.sendOtp(request.getEmail(), otp, 300); // 300 seconds = 5 minutes

        return modelMapper.map(appUser, AppUserResponse.class);
    }

    @Override
    public boolean getOtp(String email) {
        try {
            String generatedOtp = otpService.generateOtp();
            log.info("Generated OTP: {}", generatedOtp);
            otpService.sendOtp(email, generatedOtp, 120);
            return true;
        } catch (Exception e) {
            log.error("Error while generating OTP: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void authenticate(String email, String otp) {
        if (email == null || otp == null) {
            throw new BadRequestException("Email or OTP cannot be null");
        }

        AppUser user = appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new BadRequestException("The email address provided is not registered."));

        if (user.isVerified()) {
            throw new BadRequestException("Account is already verified.");
        }

        boolean verified = otpService.verifyOtp(email, otp);
        if (!verified) {
            throw new BadRequestException("The OTP entered is invalid or has expired.");
        }

        appUserRepository.verifyUser(email);
    }

    @Override
    public void resendOtp(String email) {
        AppUser existingUser = appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new BadRequestException("This email is not registered. Please register first."));

        if (existingUser.isVerified()) {
            throw new BadRequestException("Account is already verified.");
        }
        getOtp(email);
    }

    @Override
    public void forgotPassword(String email) {
        // 1. Check email exists
        appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email is not registered."));

        // 2. Generate and send OTP (reuse existing OTP flow)
        getOtp(email);
        log.info("Forgot password OTP sent to: {}", email);
    }

    @Override
    public String verifyForgotPassword(String email, String otp) {
        // 1. Check email exists
        appUserRepository.getUserByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email is not registered."));

        // 2. Verify OTP
        if (!otpService.verifyOtp(email, otp)) {
            throw new BadRequestException("OTP is invalid or has expired.");
        }

        // 3. Generate reset token and return it
        String resetToken = otpService.generateResetToken(email);
        log.info("Reset token generated for: {}", email);
        return resetToken;
    }

    @Override
    public void resetPassword(String resetToken, String newPassword) {
        // 1. Validate reset token → get email
        String email = otpService.validateResetToken(resetToken);

        // 2. Encode and update password
        String encoded = passwordEncoder.encode(newPassword);
        appUserRepository.updatePassword(email, encoded);
        log.info("Password reset for: {}", email);
    }
}