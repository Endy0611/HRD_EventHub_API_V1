package com.ksga.eventhub.controller;

import com.ksga.eventhub.exception.BadRequestException;
import com.ksga.eventhub.jwt.JwtService;
import com.ksga.eventhub.model.dto.ApiResponse;
import com.ksga.eventhub.model.dto.auth.request.*;
import com.ksga.eventhub.model.dto.auth.response.AppUserResponse;
import com.ksga.eventhub.model.dto.auth.response.AuthResponse;
import com.ksga.eventhub.model.entity.AppUser;
import com.ksga.eventhub.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService appUserService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException e) {
            throw new BadRequestException("Account is disabled.");
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid username, email, or password. Please check your credentials and try again.");
        }
    }


    @Operation(summary = "User Login")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody @Valid AuthRequest request) throws Exception{

        authenticate(request.getIdentifier(), request.getPassword());
        final UserDetails userDetails = appUserService.loadUserByUsername(request.getIdentifier());

        AppUser user = (AppUser) userDetails;

        if (!user.isVerified()) {
            throw new BadRequestException("Account is not verified. Please verify first.");
        }

        final String token = jwtService.generateToken(userDetails);
        AuthResponse authResponse = new AuthResponse(token);
        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful! Authentication token generated.")
                .status(HttpStatus.OK)
                .payload(authResponse)
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AppUserResponse>> register(@RequestBody @Valid AppUserRequest request){
        AppUserResponse appUserResponse = appUserService.register(request);

        ApiResponse<AppUserResponse> apiResponse = ApiResponse.<AppUserResponse>builder()
                .success(true)
                .message("User registered successfully! Please verify your email to complete the registration.")
                .status(HttpStatus.CREATED)
                .payload(appUserResponse)
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }


    @Operation(summary = "Verify email with OTP")
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AppUser>> verifyOtp(@RequestParam String email,
                                                          @RequestParam String otp) {
        appUserService.authenticate(email, otp);
        ApiResponse<AppUser> apiResponse = ApiResponse.<AppUser>builder()
                .success(true)
                .message("Email successfully verified! You can now log in")
                .status(HttpStatus.OK)
                .payload(null)
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @Operation(summary = "Resend verification OTP")
    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<Void>> resend(@RequestParam String email) {
        appUserService.resendOtp(email);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .message("Verification OTP successfully resent to your email.")
                .status(HttpStatus.CREATED)
                .payload(null)
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request) {
        appUserService.forgotPassword(request.getEmail());
        return ApiResponse.success("OTP sent to your email.", null);
    }

    @PostMapping("/verify-forgot-password")
    public ResponseEntity<ApiResponse<String>> verifyForgotPassword(
            @RequestBody @Valid VerifyForgotPasswordRequest request) {
        String resetToken = appUserService.verifyForgotPassword(
                request.getEmail(), request.getOtp());
        return ApiResponse.success("OTP verified.", resetToken);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {
        appUserService.resetPassword(request.getResetToken(), request.getNewPassword());
        return ApiResponse.success("Password reset successfully.", null);
    }
}