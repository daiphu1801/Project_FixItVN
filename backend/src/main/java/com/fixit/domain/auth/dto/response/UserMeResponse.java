package com.fixit.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserMeResponse {
    private UUID userId;
    private String phoneNumber;
    private String email;
    private String role;
    private String avatarUrl;
    private Boolean active;
}