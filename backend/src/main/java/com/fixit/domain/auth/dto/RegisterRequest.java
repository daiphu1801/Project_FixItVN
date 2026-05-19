//package com.fixit.domain.auth.dto;
//
//import com.fixit.domain.user.entity.User;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class RegisterRequest {
//    @NotBlank(message = "Số điện thoại không được để trống")
//    private String phone;
//
//    @NotBlank(message = "Mật khẩu không được để trống")
//    private String password;
//
//    @NotBlank(message = "Họ tên không được để trống")
//    private String fullName;
//
//    @NotNull(message = "Vai trò không được để trống")
//    private User.Role role;
//}
