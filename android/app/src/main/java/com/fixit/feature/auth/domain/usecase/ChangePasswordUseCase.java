package com.fixit.feature.auth.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;

public class ChangePasswordUseCase {
    private final AuthRepository repository;

    @Inject
    public ChangePasswordUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public void execute(String oldPassword, String newPassword, ResultCallback<Void> callback) {
        repository.changePassword(oldPassword, newPassword, callback);
    }
}
