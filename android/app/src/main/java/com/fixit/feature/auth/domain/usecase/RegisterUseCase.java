package com.fixit.feature.auth.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class RegisterUseCase {
    private final AuthRepository authRepository;

    @Inject
    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String phone,String email, String password, String fullName, String role, ResultCallback<Session> callback) {
        authRepository.register(phone,email ,password, fullName, role, callback);
    }
}
