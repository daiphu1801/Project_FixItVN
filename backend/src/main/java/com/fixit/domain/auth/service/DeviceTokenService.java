package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.request.DeviceTokenRequest;

import java.util.UUID;

public interface DeviceTokenService {
    void addDeviceToken(UUID userId, DeviceTokenRequest request);
    void removeDeviceToken(UUID userId, String deviceToken);
}
