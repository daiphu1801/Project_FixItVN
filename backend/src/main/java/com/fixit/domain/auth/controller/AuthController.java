package com.fixit.domain.auth.controller;

import com.fixit.domain.auth.dto.request.*;
import com.fixit.domain.auth.dto.response.AuthResponse;
import com.fixit.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        // =========================
        // REGISTER
        // =========================

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(
                        @Valid @RequestBody RegisterRequest request) {

                return ResponseEntity.ok(
                                authService.register(request));
        }

        // =========================
        // LOGIN
        // =========================

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                        @Valid @RequestBody LoginRequest request) {

                return ResponseEntity.ok(
                                authService.login(request));
        }

        // =========================
        // GOOGLE LOGIN
        // =========================

        @PostMapping("/login/google")
        public ResponseEntity<AuthResponse> googleLogin(
                        @Valid @RequestBody GoogleLoginRequest request) {

                return ResponseEntity.ok(
                                authService.googleLogin(request));
        }

        // =========================
        // SEND OTP
        // =========================

        @PostMapping("/otp/send")
        public ResponseEntity<String> sendOtp(
                        @Valid @RequestBody SendOtpRequest request) {

                authService.sendOtp(request);

                return ResponseEntity.ok(
                                "Mã OTP đã được gửi thành công");
        }

        // =========================
        // VERIFY OTP
        // =========================

        @PostMapping("/otp/verify")
        public ResponseEntity<AuthResponse> verifyOtp(
                        @Valid @RequestBody VerifyOtpRequest request) {

                return ResponseEntity.ok(
                                authService.verifyOtp(request));
        }

        // =========================
        // FORGOT PASSWORD
        // =========================

        @PostMapping("/forgot-password")
        public ResponseEntity<String> forgotPassword(
                        @Valid @RequestBody ForgotPasswordRequest request) {

                authService.forgotPassword(request);

                return ResponseEntity.ok(
                                "OTP đặt lại mật khẩu đã được gửi");
        }

        // =========================
        // RESET PASSWORD
        // =========================

        @PostMapping("/reset-password")
        public ResponseEntity<String> resetPassword(
                        @Valid @RequestBody ResetPasswordRequest request) {

                authService.resetPassword(request);

                return ResponseEntity.ok(
                                "Mật khẩu đã được đặt lại thành công");
        }

        // =========================
        // CHANGE PASSWORD
        // =========================

        @PatchMapping("/change-password")
        public ResponseEntity<String> changePassword(
                        @Valid @RequestBody ChangePasswordRequest request,
                        Authentication authentication) {

                // Email hoặc phone lấy từ JWT token
                String identifier = authentication.getName();

                authService.changePassword(
                                identifier,
                                request);

                return ResponseEntity.ok(
                                "Đổi mật khẩu thành công");
        }

        // =========================
        // REFRESH TOKEN
        // =========================

        @PostMapping("/refresh-token")
        public ResponseEntity<AuthResponse> refreshToken(
                        @Valid @RequestBody TokenRefreshRequest request) {

                return ResponseEntity.ok(
                                authService.refreshToken(request));
        }

        // =========================
        // LOGOUT
        // =========================

        @PostMapping("/logout")
        public ResponseEntity<String> logout(
                        @RequestParam String refreshToken) {

                authService.logout(refreshToken);

                return ResponseEntity.ok(
                                "Đăng xuất thành công");
        }
}