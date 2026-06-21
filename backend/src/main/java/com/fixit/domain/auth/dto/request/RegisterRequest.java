package com.fixit.domain.auth.dto.request;

import com.fixit.domain.auth.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    // Identifier could be phone or email
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[.,!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|<>/?])[A-Z].{7,}$",
        message = "Mật khẩu phải từ 8 ký tự trở lên, bắt đầu bằng chữ viết hoa, chứa ít nhất một chữ số và một ký tự đặc biệt (ví dụ: .,!)"
    )
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotNull(message = "Vai trò không được để trống")
    private UserRole role;
}
