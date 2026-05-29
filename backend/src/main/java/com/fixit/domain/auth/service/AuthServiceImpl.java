package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.request.*;
import com.fixit.domain.auth.dto.response.AuthResponse;
import com.fixit.domain.auth.entity.*;
import com.fixit.domain.auth.repository.OtpCodeRepository;
import com.fixit.domain.auth.repository.RefreshTokenRepository;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.domain.auth.repository.UserSocialLoginRepository;
import com.fixit.domain.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final UserSocialLoginRepository userSocialLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getIdentifier()) || userRepository.existsByEmail(request.getIdentifier())) {
            throw new RuntimeException("Tài khoản (SĐT/Email) đã được đăng ký");
        }

        boolean isEmail = request.getIdentifier().contains("@");

        User user = User.builder()
                .phoneNumber(isEmail ? null : request.getIdentifier())
                .email(isEmail ? request.getIdentifier() : null)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );

        User user = userRepository.findByPhoneNumber(request.getIdentifier())
                .orElseGet(() -> userRepository.findByEmail(request.getIdentifier())
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        String googleId = "mock-google-id-" + UUID.randomUUID().toString().substring(0,8);
        String email = "mock@gmail.com";
        String name = "Mock Google User";

        UserSocialLogin socialLogin = userSocialLoginRepository.findByProviderAndProviderId("GOOGLE", googleId)
                .orElse(null);

        User user;
        if (socialLogin == null) {
            user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = User.builder()
                        .email(email)
                        .role(UserRole.Customer)
                        .active(true)
                        .build();
                userRepository.save(user);
            }
            UserSocialLogin newSocialLogin = UserSocialLogin.builder()
                    .user(user)
                    .provider("GOOGLE")
                    .providerId(googleId)
                    .build();
            userSocialLoginRepository.save(newSocialLogin);
        } else {
            user = socialLogin.getUser();
        }

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void sendOtp(SendOtpRequest request) {
        otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(request.getIdentifier(), request.getActionType())
                .ifPresent(otp -> {
                    otp.setUsed(true);
                    otpCodeRepository.save(otp);
                });

        String code = String.format("%06d", new Random().nextInt(999999));
        
        OtpCode otpCode = OtpCode.builder()
                .phoneNumber(request.getIdentifier())
                .otpCode(code)
                .actionType(request.getActionType())
                .expiresAt(OffsetDateTime.now().plusMinutes(2))
                .used(false)
                .build();
                
        otpCodeRepository.save(otpCode);
        log.info("Mã OTP gửi đến {}: {}", request.getIdentifier(), code);
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        OtpCode otp = otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(request.getIdentifier(), request.getActionType())
                .orElseThrow(() -> new RuntimeException("Mã OTP không tồn tại hoặc đã được sử dụng"));

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new RuntimeException("Mã OTP không chính xác");
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        User user = userRepository.findByPhoneNumber(request.getIdentifier())
                .orElseGet(() -> userRepository.findByEmail(request.getIdentifier())
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        OtpCode otp = otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(request.getIdentifier(), OtpActionType.FORGOT_PASSWORD)
                .orElseThrow(() -> new RuntimeException("Mã OTP không tồn tại hoặc đã được sử dụng"));

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new RuntimeException("Mã OTP không chính xác");
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        User user = userRepository.findByPhoneNumber(request.getIdentifier())
                .orElseGet(() -> userRepository.findByEmail(request.getIdentifier())
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String identifier, ChangePasswordRequest request) {
        User user = userRepository.findByPhoneNumber(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken savedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại"));

        if (savedToken.getRevoked()) {
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }

        if (savedToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(savedToken);
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        User user = savedToken.getUser();
        String newAccessToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(savedToken.getToken())
                .user(mapToUserInfo(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshTokenStr = jwtService.generateRefreshToken(user);

        refreshTokenRepository.findByUserId(user.getId()).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .user(mapToUserInfo(user))
                .build();
    }

    private AuthResponse.UserInfo mapToUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .phone(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
