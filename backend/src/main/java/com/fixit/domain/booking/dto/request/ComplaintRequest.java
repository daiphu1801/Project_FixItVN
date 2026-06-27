package com.fixit.domain.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class ComplaintRequest {
    @NotBlank(message = "Lý do khiếu nại không được để trống")
    private String customerReason;

    private List<String> evidenceImageUrls;
}
