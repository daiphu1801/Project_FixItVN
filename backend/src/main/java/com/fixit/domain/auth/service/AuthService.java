package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.request.*;
import com.fixit.domain.auth.dto.response.AuthResponse;
import com.fixit.domain.auth.dto.response.NotificationResponse;
import com.fixit.domain.auth.dto.response.UnreadCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AuthService {

    // =========================
    // AUTHENTICATION
    // =========================

    // Đăng ký tài khoản
    AuthResponse register(RegisterRequest request);

    // Đăng nhập bằng email/sđt + password
    AuthResponse login(LoginRequest request);

    // Đăng nhập Google
    AuthResponse googleLogin(GoogleLoginRequest request);

    // Refresh JWT token
    AuthResponse refreshToken(TokenRefreshRequest request);

    // Logout
    void logout(String refreshToken);

    // =========================
    // OTP
    // =========================

    // Gửi OTP
    void sendOtp(SendOtpRequest request);

    // Verify OTP
    AuthResponse verifyOtp(VerifyOtpRequest request);

    // =========================
    // PASSWORD
    // =========================

    // Quên mật khẩu
    void forgotPassword(ForgotPasswordRequest request);

    // Reset mật khẩu bằng token/otp
    void resetPassword(ResetPasswordRequest request);

    // Đổi mật khẩu khi đã login
    void changePassword(
            String identifier,
            ChangePasswordRequest request);

    @Transactional
    void registerDeviceToken(String identifier, DeviceTokenRequest request);

    void removeDeviceToken(String deviceToken);

    Page<NotificationResponse> getMyNotifications(String identifier, Pageable pageable);

    UnreadCountResponse getMyUnreadCount(String identifier);

    void markAsRead(String identifier, UUID notificationId);

    void markAllAsRead(String identifier);
}
