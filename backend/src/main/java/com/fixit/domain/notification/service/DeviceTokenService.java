package com.fixit.domain.notification.service;

import com.fixit.domain.notification.dto.request.DeviceTokenRequest;

import java.util.UUID;

public interface DeviceTokenService {
    void addDeviceToken(UUID userId, DeviceTokenRequest request);
    void removeDeviceToken(UUID userId, String deviceToken);
}
