package com.fixit.domain.notification.dto.request;

import com.fixit.domain.notification.entity.DeviceOs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenRequest {

    @NotBlank(message = "Device token không được để trống")
    private String deviceToken;

    @NotNull(message = "Hệ điều hành thiết bị không được để trống")
    private DeviceOs deviceOs;
}
