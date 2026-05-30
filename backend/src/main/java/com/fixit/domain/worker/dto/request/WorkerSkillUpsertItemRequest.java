package com.fixit.domain.worker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerSkillUpsertItemRequest {

    @NotNull(message = "serviceId không được để trống")
    private Integer serviceId;

    @DecimalMin(value = "0.0", inclusive = true, message = "basePrice không được âm")
    private BigDecimal basePrice;
}