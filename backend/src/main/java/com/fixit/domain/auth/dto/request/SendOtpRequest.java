package com.fixit.domain.auth.dto.request;

import com.fixit.domain.auth.entity.OtpActionType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendOtpRequest {

    private String phoneNumber;

    private String email;
    private String identifier;

    @NotNull(message = "Loại hành động không được để trống")
    private OtpActionType actionType;
}
