package com.ksga.eventhub.service;

import com.ksga.eventhub.model.dto.auth.request.AppUserRequest;
import com.ksga.eventhub.model.dto.auth.response.AppUserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
    UserDetails loadUserByUsername(@NotBlank @NotNull String email);

    AppUserResponse register(@Valid AppUserRequest request);

    boolean getOtp(String email);

    void authenticate(String email, String otp);

    void resendOtp(String email);

    void forgotPassword(String email);

    String verifyForgotPassword(String email, String otp);

    void resetPassword(String resetToken, String newPassword);
}
