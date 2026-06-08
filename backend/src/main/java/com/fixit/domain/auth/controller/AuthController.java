package com.fixit.domain.auth.controller;

import com.fixit.domain.auth.dto.request.*;
import com.fixit.domain.auth.dto.response.AuthResponse;
import com.fixit.domain.auth.dto.response.NotificationResponse;
import com.fixit.domain.auth.dto.response.UnreadCountResponse;
import com.fixit.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

        // =========================
        // DEVICE TOKEN
        // =========================

        @PostMapping("/device-tokens")
        public ResponseEntity<String> registerDeviceToken(
                        Authentication authentication,
                        @Valid @RequestBody DeviceTokenRequest request) {
                
                String identifier = authentication.getName();
                authService.registerDeviceToken(identifier, request);
                return ResponseEntity.ok("Đăng ký device token thành công");
        }

        @DeleteMapping("/device-tokens/{deviceToken}")
        public ResponseEntity<Void> removeDeviceToken(
                        @PathVariable String deviceToken) {
                
                authService.removeDeviceToken(deviceToken);
                return ResponseEntity.noContent().build();
        }

        // =========================
        // NOTIFICATIONS
        // =========================

        @GetMapping("/notifications")
        public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
                Authentication authentication,
                @PageableDefault(size = 20) Pageable pageable) {
                
                String identifier = authentication.getName();
                return ResponseEntity.ok(authService.getMyNotifications(identifier, pageable));
        }

        @GetMapping("/notifications/unread-count")
        public ResponseEntity<UnreadCountResponse> getMyUnreadCount(Authentication authentication) {
                String identifier = authentication.getName();
                return ResponseEntity.ok(authService.getMyUnreadCount(identifier));
        }

        @PatchMapping("/notifications/{notificationId}/read")
        public ResponseEntity<String> markAsRead(
                Authentication authentication,
                @PathVariable UUID notificationId) {
                
                String identifier = authentication.getName();
                authService.markAsRead(identifier, notificationId);
                return ResponseEntity.ok("Đã đánh dấu thông báo là đã đọc");
        }

        @PatchMapping("/notifications/read-all")
        public ResponseEntity<String> markAllAsRead(Authentication authentication) {
                String identifier = authentication.getName();
                authService.markAllAsRead(identifier);
                return ResponseEntity.ok("Đã đánh dấu tất cả thông báo là đã đọc");
        }
}