package com.fixit.domain.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProofOfWorkCreateRequest {

    @NotNull(message = "uploadId không được để trống")
    private UUID uploadId;

    @NotBlank(message = "proofType không được để trống")
    private String proofType;
}