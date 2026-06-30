package com.fixit.feature.auth.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class ResetPasswordUseCase {
    private final AuthRepository authRepository;

    @Inject
    public ResetPasswordUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String phone, String otpCode, String newPassword, ResultCallback<Void> callback) {
        authRepository.resetPassword(email, phone, otpCode, newPassword, callback);
    }
}
