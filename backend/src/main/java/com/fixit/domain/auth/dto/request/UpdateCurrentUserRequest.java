package com.fixit.domain.auth.dto.request;



import lombok.Data;



@Data
public class UpdateCurrentUserRequest {

    private String fullName;

    private String phoneNumber;

    private String avatarUrl;
}
