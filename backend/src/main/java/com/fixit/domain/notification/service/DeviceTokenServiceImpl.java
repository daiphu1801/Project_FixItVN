package com.fixit.domain.notification.service;

import com.fixit.domain.notification.dto.request.DeviceTokenRequest;
import com.fixit.domain.auth.entity.User;
import com.fixit.domain.notification.entity.UserDevice;
import com.fixit.domain.notification.repository.UserDeviceRepository;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addDeviceToken(UUID userId, DeviceTokenRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Optional<UserDevice> existingDeviceOpt = userDeviceRepository.findByDeviceToken(request.getDeviceToken());

        if (existingDeviceOpt.isPresent()) {
            UserDevice existingDevice = existingDeviceOpt.get();
            // If the token is already assigned to a different user, reassign it
            if (!existingDevice.getUser().getId().equals(userId)) {
                existingDevice.setUser(user);
            }
            existingDevice.setDeviceOs(request.getDeviceOs());
            existingDevice.setLastActive(OffsetDateTime.now());
            userDeviceRepository.save(existingDevice);
        } else {
            UserDevice newDevice = UserDevice.builder()
                    .user(user)
                    .deviceToken(request.getDeviceToken())
                    .deviceOs(request.getDeviceOs())
                    .lastActive(OffsetDateTime.now())
                    .build();
            userDeviceRepository.save(newDevice);
        }
    }

    @Override
    @Transactional
    public void removeDeviceToken(UUID userId, String deviceToken) {
        userDeviceRepository.deleteByUserIdAndDeviceToken(userId, deviceToken);
    }
}
