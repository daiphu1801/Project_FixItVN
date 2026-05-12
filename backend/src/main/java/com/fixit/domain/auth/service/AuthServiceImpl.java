package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.AuthResponse;
import com.fixit.domain.auth.dto.LoginRequest;
import com.fixit.domain.auth.dto.RegisterRequest;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    // JWT service will be added later

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã được đăng ký");
        }

        User user = User.builder()
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .active(true)
                .build();

        userRepository.save(user);

        // Placeholder for token generation
        return AuthResponse.builder()
                .accessToken("dummy-access-token")
                .refreshToken("dummy-refresh-token")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .phone(user.getPhone())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
        );

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Placeholder for token generation
        return AuthResponse.builder()
                .accessToken("dummy-access-token")
                .refreshToken("dummy-refresh-token")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .phone(user.getPhone())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
