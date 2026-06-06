package com.fixit.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserAvatarUpdateRequest {

    @NotNull(message = "uploadId không được để trống")
    private UUID uploadId;
}