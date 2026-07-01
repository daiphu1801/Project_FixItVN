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
import com.fixit.domain.customer.entity.Customer;
import com.fixit.domain.customer.repository.CustomerRepository;
import com.fixit.domain.wallet.repository.WorkerWalletRepository;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.entity.WorkerVerificationStatus;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.email.service.EmailService;
import com.fixit.global.security.JwtService;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String phone = request.getPhone().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByPhoneNumber(phone)) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
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
            Customer customer = Customer.builder()
                    .user(savedUser)
                    .fullName(request.getFullName().trim())
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
                .findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new AppException(ErrorCode.USER_BLOCKED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Kiểm tra role: nếu client khai báo role và không khớp với role thực tế của user
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            String clientRole = request.getRole().trim();
            String actualRole = user.getRole() != null ? user.getRole().name() : "";
            if (!clientRole.equalsIgnoreCase(actualRole)) {
                throw new AppException(ErrorCode.WRONG_ROLE);
            }
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
        String googleId = "mock-google-id-" + UUID.randomUUID().toString().substring(0, 8);
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
                User savedUser = userRepository.save(user);

                Customer customer = Customer.builder()
                        .user(savedUser)
                        .fullName(name)
                        .build();
                customerRepository.save(customer);
                user = savedUser;
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
        String phoneNumber = request.getPhoneNumber();
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;

        if (phoneNumber != null) {
            otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(phoneNumber, request.getActionType())
                    .ifPresent(otp -> {
                        otp.setUsed(true);
                        otpCodeRepository.save(otp);
                    });
        }
        if (email != null) {
            otpCodeRepository.findByEmailAndActionTypeAndUsedFalse(email, request.getActionType())
                    .ifPresent(otp -> {
                        otp.setUsed(true);
                        otpCodeRepository.save(otp);
                    });
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        OtpCode otpCode = OtpCode.builder()
                .phoneNumber(phoneNumber)
                .email(email)
                .otpCode(code)
                .actionType(request.getActionType())
                .expiresAt(OffsetDateTime.now().plusMinutes(2))
                .used(false)
                .build();

        otpCodeRepository.save(otpCode);

        if (email != null) {
            log.info("Mã OTP gửi đến email {}: {}", email, code);
            emailService.sendOtpEmail(email, code);
        } else if (phoneNumber != null) {
            log.info("Mã OTP gửi đến phone {}: {}", phoneNumber, code);
        }
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        OtpCode otp;
        if (email != null) {
            otp = otpCodeRepository.findByEmailAndActionTypeAndUsedFalse(email, request.getActionType())
                    .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
        } else {
            otp = otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(phoneNumber, request.getActionType())
                    .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
        }

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        User user = email != null
                ? userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
                : userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        OtpCode otp;
        if (email != null && !email.isEmpty()) {
            otp = otpCodeRepository.findByEmailAndActionTypeAndUsedFalse(email, OtpActionType.FORGOT_PASSWORD)
                    .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
        } else if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            otp = otpCodeRepository.findByPhoneNumberAndActionTypeAndUsedFalse(phoneNumber, OtpActionType.FORGOT_PASSWORD)
                    .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
        } else {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER, "Email hoặc số điện thoại không được để trống");
        }

        if (otp.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        User user = (email != null && !email.isEmpty())
                ? userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
                : userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String identifier, ChangePasswordRequest request) {
        String parsedIdentifier = identifier != null ? identifier.trim() : "";
        if (parsedIdentifier.contains("@")) {
            parsedIdentifier = parsedIdentifier.toLowerCase();
        }
        String finalIdentifier = parsedIdentifier;
        User user = userRepository.findByPhoneNumber(finalIdentifier)
                .orElseGet(() -> userRepository.findByEmail(finalIdentifier)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken savedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "Refresh token không tồn tại"));

        if (savedToken.getRevoked()) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Refresh token đã bị thu hồi");
        }

        if (savedToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(savedToken);
            throw new AppException(ErrorCode.UNAUTHORIZED, "Refresh token đã hết hạn");
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

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        String phoneNumber = request.getPhoneNumber();

        if (email != null && !email.isEmpty()) {
            userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            SendOtpRequest sendOtpRequest = SendOtpRequest.builder()
                    .email(email)
                    .actionType(OtpActionType.FORGOT_PASSWORD)
                    .build();
            sendOtp(sendOtpRequest);
        } else if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            SendOtpRequest sendOtpRequest = SendOtpRequest.builder()
                    .phoneNumber(phoneNumber)
                    .actionType(OtpActionType.FORGOT_PASSWORD)
                    .build();
            sendOtp(sendOtpRequest);
        } else {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER, "Email hoặc số điện thoại không được để trống");
        }
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
        String fullName = null;
        if (user.getRole() == UserRole.Customer) {
            fullName = customerRepository.findById(user.getId())
                    .map(Customer::getFullName)
                    .orElse(null);
        } else if (user.getRole() == UserRole.Worker) {
            fullName = workerRepository.findById(user.getId())
                    .map(Worker::getFullName)
                    .orElse(null);
        }

        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .phone(user.getPhoneNumber())
                .email(user.getEmail())
                .fullName(fullName)
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}