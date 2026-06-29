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
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    // Role mà client đang đăng nhập ("Customer" hoặc "Worker")
    // Dùng để kiểm tra tài khoản có đúng vai trò không
    private String role;
}
