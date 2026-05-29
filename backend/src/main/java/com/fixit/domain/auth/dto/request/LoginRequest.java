package com.fixit.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Số điện thoại/Email không được để trống")
    private String identifier; // phone or email

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
