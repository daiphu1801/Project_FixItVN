package com.fixit.domain.worker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkerStatusUpdateRequest {

    @NotNull(message = "Trạng thái nhận việc không được để trống")
    private Boolean available;
}