package com.fixit.feature.auth.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class LoginWithGoogleUseCase {
    private final AuthRepository authRepository;

    @Inject
    public LoginWithGoogleUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String idToken, String role, ResultCallback<Session> callback) {
        authRepository.loginWithGoogle(idToken, role, callback);
    }
}
