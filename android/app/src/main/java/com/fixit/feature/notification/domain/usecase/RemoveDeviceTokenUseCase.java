package com.fixit.feature.notification.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.notification.domain.repository.NotificationRepository;

import javax.inject.Inject;

public class RemoveDeviceTokenUseCase {

    private final NotificationRepository repository;

    @Inject
    public RemoveDeviceTokenUseCase(NotificationRepository repository) {
        this.repository = repository;
    }

    public void execute(String deviceToken, ResultCallback<Void> callback) {
        repository.removeDeviceToken(deviceToken, callback);
    }
}
