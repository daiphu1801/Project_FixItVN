package com.fixit.feature.notification.domain.repository;

import com.fixit.core.common.ResultCallback;

public interface NotificationRepository {
    void registerDeviceToken(String deviceToken, String deviceOs, ResultCallback<Void> callback);
    void removeDeviceToken(String deviceToken, ResultCallback<Void> callback);
}
