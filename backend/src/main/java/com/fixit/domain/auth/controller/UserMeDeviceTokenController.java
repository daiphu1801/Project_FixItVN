package com.fixit.domain.auth.controller;

import com.fixit.domain.auth.dto.request.DeviceTokenRequest;
import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.service.DeviceTokenService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/device-tokens")
@RequiredArgsConstructor
public class UserMeDeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addDeviceToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DeviceTokenRequest request) {
        deviceTokenService.addDeviceToken(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{deviceToken}")
    public ResponseEntity<ApiResponse<Void>> removeDeviceToken(
            @AuthenticationPrincipal User user,
            @PathVariable String deviceToken) {
        deviceTokenService.removeDeviceToken(user.getId(), deviceToken);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
