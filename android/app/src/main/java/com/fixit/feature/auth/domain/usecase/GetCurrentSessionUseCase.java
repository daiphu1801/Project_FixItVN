package com.fixit.feature.auth.domain.usecase;

import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class GetCurrentSessionUseCase {
    private final AuthRepository authRepository;

    @Inject
    public GetCurrentSessionUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Session execute() {
        return authRepository.getCurrentSession();
    }
}
