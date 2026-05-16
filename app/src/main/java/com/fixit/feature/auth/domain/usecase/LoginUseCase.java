package com.fixit.feature.auth.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class LoginUseCase {
    private final AuthRepository authRepository;

    @Inject
    public LoginUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String phone, String password, String role, ResultCallback<Session> callback) {
        authRepository.login(phone, password, role, callback);
    }
}
