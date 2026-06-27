package com.fixit.feature.auth.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class RefreshTokenUseCase {
    private final AuthRepository authRepository;

    @Inject
    public RefreshTokenUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String refreshToken, ResultCallback<Session> callback) {
        authRepository.refreshToken(refreshToken, callback);
    }
}
