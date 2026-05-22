package com.fixit.domain.worker.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerSkillResponse {

    private Integer serviceId;

    private String serviceName;

    private BigDecimal basePrice;
}