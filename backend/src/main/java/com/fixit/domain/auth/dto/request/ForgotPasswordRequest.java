package com.fixit.domain.auth.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
    private String phoneNumber;
}
