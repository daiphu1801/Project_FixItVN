package com.fixit.feature.notification.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.notification.domain.repository.NotificationRepository;

import javax.inject.Inject;

public class RegisterDeviceTokenUseCase {

    private final NotificationRepository repository;

    @Inject
    public RegisterDeviceTokenUseCase(NotificationRepository repository) {
        this.repository = repository;
    }

    public void execute(String deviceToken, String deviceOs, ResultCallback<Void> callback) {
        repository.registerDeviceToken(deviceToken, deviceOs, callback);
    }
}
