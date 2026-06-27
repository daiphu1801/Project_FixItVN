package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.request.*;
import com.fixit.domain.auth.dto.response.AuthResponse;
import com.fixit.domain.auth.entity.*;
import com.fixit.domain.auth.repository.OtpCodeRepository;
import com.fixit.domain.auth.repository.RefreshTokenRepository;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.domain.auth.repository.UserSocialLoginRepository;
import com.fixit.domain.customer.entity.Customer;
import com.fixit.domain.customer.repository.CustomerRepository;
import com.fixit.domain.wallet.entity.WorkerWallet;
import com.fixit.domain.wallet.repository.WorkerWalletRepository;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.entity.WorkerVerificationStatus;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.global.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;
    private final WorkerWalletRepository workerWalletRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final UserSocialLoginRepository userSocialLoginRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String phone = request.getPhone().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByPhoneNumber(phone)) {
            throw new RuntimeException("Số điện thoại đã được đăng ký");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã được đăng ký");
        }

        User user = User.builder()
                .phoneNumber(phone)
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        if (request.getRole() == UserRole.Worker) {
            Worker worker = Worker.builder()
                    .user(savedUser)
                    .fullName(request.getFullName().trim())
                    .verificationStatus(WorkerVerificationStatus.Unverified)
                    .available(false)
                    .reputationScore(BigDecimal.valueOf(5.0))
                    .missedCount(0)
                    .rejectionCount(0)
                    .build();

            Worker savedWorker = workerRepository.save(worker);

            WorkerWallet wallet = WorkerWallet.builder()
                    .worker(savedWorker)
                    .availableBalance(BigDecimal.ZERO)
                    .heldBalance(BigDecimal.ZERO)
                    .debtBalance(BigDecimal.ZERO)
                    .build();

            workerWalletRepository.save(wallet);
        } else if (request.getRole() == UserRole.Customer) {
            String customerName = (request.getFullName() != null && !request.getFullName().trim().isEmpty()) 
                    ? request.getFullName().trim() : "Khách hàng mới";
            Customer customer = Customer.builder()
                    .user(savedUser)
                    .fullName(customerName)
                    .build();
            customerRepository.save(customer);
        }

        String accessToken = jwtService.generateToken(savedUser);
        String refreshTokenValue = jwtService.generateRefreshToken(savedUser);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(savedUser)
                .token(refreshTokenValue)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(savedUser, accessToken, refreshTokenValue);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findByPhoneNumber(request.getIdentifier())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        refreshTokenRepository.revokeAllActiveTokensByUserId(user.getId());

        String accessToken = jwtService.generateToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(user, accessToken, refreshTokenValue);
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
                user = userRepository.save(user);

                Customer customer = Customer.builder()
                        .user(user)
                        .fullName(name)
                        .build();
                customerRepository.save(customer);
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

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserInfo(user))
                .build();
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        refreshTokenRepository.revokeAllActiveTokensByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return buildAuthResponse(user, accessToken, refreshTokenValue);
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
