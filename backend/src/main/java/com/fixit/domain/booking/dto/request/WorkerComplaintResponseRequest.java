package com.fixit.domain.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class WorkerComplaintResponseRequest {
    @NotBlank(message = "Nội dung giải trình không được để trống")
    private String workerResponse;

    private List<String> evidenceImageUrls;
}
