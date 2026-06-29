package com.fixit.feature.auth.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class ForgotPasswordUseCase {
    private final AuthRepository authRepository;

    @Inject
    public ForgotPasswordUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String phone, ResultCallback<Void> callback) {
        authRepository.forgotPassword(email, phone, callback);
    }
}
