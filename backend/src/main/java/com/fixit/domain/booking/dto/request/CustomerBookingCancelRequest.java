package com.fixit.domain.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerBookingCancelRequest {

    @NotBlank(message = "Vui lòng chọn lý do hủy")
    private String reason;

    private boolean isWorkerFault;

}
