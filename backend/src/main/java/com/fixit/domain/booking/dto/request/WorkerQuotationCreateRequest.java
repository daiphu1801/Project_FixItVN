package com.fixit.domain.booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkerQuotationCreateRequest {
    @NotNull(message = "Tiền công không được để trống")
    @Min(value = 0, message = "Tiền công không được âm")
    private BigDecimal laborCost;

    @NotNull(message = "Tiền vật tư không được để trống")
    @Min(value = 0, message = "Tiền vật tư không được âm")
    private BigDecimal materialCost;
}
