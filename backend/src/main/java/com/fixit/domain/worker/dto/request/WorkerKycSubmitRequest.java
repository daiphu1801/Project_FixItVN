package com.fixit.domain.worker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class WorkerKycSubmitRequest {

    @NotNull(message = "frontImageUploadId không được để trống")
    private UUID frontImageUploadId;

    @NotNull(message = "backImageUploadId không được để trống")
    private UUID backImageUploadId;

    private List<UUID> certificateUploadIds;
}