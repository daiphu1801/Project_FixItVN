package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.request.*;
import com.fixit.domain.auth.dto.response.AuthResponse;
import com.fixit.domain.auth.dto.response.NotificationResponse;
import com.fixit.domain.auth.dto.response.UnreadCountResponse;
import com.fixit.domain.auth.entity.*;
import com.fixit.domain.auth.repository.OtpCodeRepository;
import com.fixit.domain.auth.repository.RefreshTokenRepository;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.domain.auth.repository.UserSocialLoginRepository;
import com.fixit.domain.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final WorkerWalletRepository workerWalletRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final UserSocialLoginRepository userSocialLoginRepository;
    private final NotificationRepository notificationRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        SendOtpRequest otpRequest = SendOtpRequest.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .actionType(OtpActionType.FORGOT_PASSWORD)
                .build();
        sendOtp(otpRequest);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.getIdentifier())
                || userRepository.existsByEmail(request.getIdentifier())) {
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

        User savedUser = userRepository.save(user);

        if (request.getRole() == UserRole.Worker) {
            Worker worker = Worker.builder()
                    .user(savedUser)
                    .fullName(request.getFullName().trim())
                    .verificationStatus(WorkerVerificationStatus.Pending)
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword()));

        User user = userRepository.findByPhoneNumber(request.getIdentifier())
                .orElseGet(() -> userRepository.findByEmail(request.getIdentifier())
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));

        return buildAuthResponse(user, accessToken, refreshTokenValue);
    }

    @Override
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        String googleId = "mock-google-id-" + UUID.randomUUID().toString().substring(0, 8);
        String email = "mock@gmail.com";

        UserSocialLogin socialLogin = userSocialLoginRepository.findByProviderAndProviderId("GOOGLE", googleId)
                .orElse(null);

        User user;
        if (socialLogin == null) {
            user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = User.builder()
                        .email(email)
                        .phoneNumber("N/A")
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
        final String identifier;
        boolean exists = false;

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            identifier = request.getEmail();
            exists = userRepository.existsByEmail(identifier);
        } else if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            identifier = request.getPhoneNumber();
            exists = userRepository.existsByPhoneNumber(identifier);
        } else {
            throw new RuntimeException("Số điện thoại hoặc Email không được để trống");
        }

        if (request.getActionType() == OtpActionType.FORGOT_PASSWORD && !exists) {
            throw new RuntimeException("Tài khoản không tồn tại");
        } else if (request.getActionType() == OtpActionType.REGISTER && exists) {
            throw new RuntimeException("Tài khoản đã được đăng ký");
        }

        otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(identifier, request.getActionType())
                .ifPresent(otp -> {
                    otp.setUsed(true);
                    otpCodeRepository.save(otp);
                });

        String code = String.format("%06d", new Random().nextInt(1000000));

        OtpCode otpCode = OtpCode.builder()
                .phoneNumber(identifier)
                .otpCode(code)
                .actionType(request.getActionType())
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpCodeRepository.save(otpCode);

        if (identifier.contains("@")) {
            sendEmailOtp(identifier, code);
        } else {
            log.info("[SMS MOCK] Gửi OTP {} đến số điện thoại: {}", code, identifier);
        }

        log.info("Mã OTP gửi đến {}: {}", identifier, code);
    }

    private void sendEmailOtp(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Mã OTP xác thực FixItVN");
            message.setText("Mã OTP của bạn là: " + code + ". Mã này có hiệu lực trong 5 phút.");

            log.info("Đã gửi email OTP thành công đến: {}", email);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email đến {}: {}", email, e.getMessage());
        }
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        final String identifier;
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            identifier = request.getEmail();
        } else if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            identifier = request.getPhoneNumber();
        } else {
            throw new RuntimeException("Số điện thoại hoặc Email không được để trống");
        }

        OtpCode otp = otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(identifier, request.getActionType())
                .orElseThrow(() -> new RuntimeException("Mã OTP không tồn tại hoặc đã được sử dụng"));

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new RuntimeException("Mã OTP không chính xác");
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        User user = userRepository.findByPhoneNumber(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        final String identifier;
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            identifier = request.getEmail();
        } else if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            identifier = request.getPhoneNumber();
        } else {
            throw new RuntimeException("Số điện thoại hoặc Email không được để trống");
        }

        OtpCode otp = otpCodeRepository
                .findByPhoneNumberAndActionTypeAndUsedFalse(identifier, OtpActionType.FORGOT_PASSWORD)
                .orElseThrow(() -> new RuntimeException("Mã OTP không tồn tại hoặc đã được sử dụng"));

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn");
        }

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new RuntimeException("Mã OTP không chính xác");
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        User user = userRepository.findByPhoneNumber(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
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

        refreshTokenRepository.findByUserId(user.getId()).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });

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

    // =========================
    // DEVICE TOKEN
    // =========================

    @Override
    @Transactional
    public void registerDeviceToken(String identifier, DeviceTokenRequest request) {
        User user = userRepository.findByPhoneNumber(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));

        Optional<UserDevice> existingDeviceOpt = userDeviceRepository.findByDeviceToken(request.getDeviceToken());

        if (existingDeviceOpt.isPresent()) {
            UserDevice existingDevice = existingDeviceOpt.get();
            existingDevice.setUser(user);
            existingDevice.setDeviceOs(request.getDeviceOs());
            existingDevice.setLastActive(OffsetDateTime.now());
            userDeviceRepository.save(existingDevice);
            log.info("Cập nhật device token cho user: {}", user.getId());
        } else {
            UserDevice newDevice = UserDevice.builder()
                    .user(user)
                    .deviceToken(request.getDeviceToken())
                    .deviceOs(request.getDeviceOs())
                    .lastActive(OffsetDateTime.now())
                    .build();
            userDeviceRepository.save(newDevice);
            log.info("Thêm mới device token cho user: {}", user.getId());
        }
    }

    @Override
    @Transactional
    public void removeDeviceToken(String deviceToken) {
        userDeviceRepository.deleteByDeviceToken(deviceToken);
        log.info("Đã xóa device token: {}", deviceToken);
    }

    // =========================
    // NOTIFICATION
    // =========================

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(String identifier, Pageable pageable) {
        User user = getUserByIdentifier(identifier);
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(),
                pageable);
        return notifications.map(this::mapToNotificationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getMyUnreadCount(String identifier) {
        User user = getUserByIdentifier(identifier);
        long count = notificationRepository.countByUserIdAndReadFalse(user.getId());
        return new UnreadCountResponse(count);
    }

    @Override
    @Transactional
    public void markAsRead(String identifier, UUID notificationId) {
        User user = getUserByIdentifier(identifier);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền truy cập thông báo này");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String identifier) {
        User user = getUserByIdentifier(identifier);
        notificationRepository.markAllAsReadByUserId(user.getId());
    }

    private User getUserByIdentifier(String identifier) {
        return userRepository.findByPhoneNumber(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại")));
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
