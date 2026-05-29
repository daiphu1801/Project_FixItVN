package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.request.*;
import com.fixit.domain.auth.dto.response.AuthResponse;

public interface AuthService {
    
    // Đăng ký tài khoản (hỗ trợ password)
    AuthResponse register(RegisterRequest request);
    
    // Đăng nhập bằng Password
    AuthResponse login(LoginRequest request);
    
    // Đăng nhập bằng Google
    AuthResponse googleLogin(GoogleLoginRequest request);
    
    // Gửi OTP (để đăng nhập OTP, quên mật khẩu, v.v.)
    void sendOtp(SendOtpRequest request);
    
    // Đăng nhập hoặc xác thực OTP
    AuthResponse verifyOtp(VerifyOtpRequest request);
    
    // Đặt lại mật khẩu (quên mật khẩu)
    void resetPassword(ResetPasswordRequest request);
    
    // Đổi mật khẩu (khi đã đăng nhập)
    void changePassword(String identifier, ChangePasswordRequest request);
    
    // Làm mới Access Token
    AuthResponse refreshToken(TokenRefreshRequest request);
    
    // Đăng xuất (xóa refresh token)
    void logout(String refreshToken);
}
