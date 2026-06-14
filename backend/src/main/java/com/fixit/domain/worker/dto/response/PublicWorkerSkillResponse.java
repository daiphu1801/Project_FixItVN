package com.fixit.domain.worker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicWorkerSkillResponse {
    private Integer serviceId;
    private String serviceName;
    private String iconUrl;
    private BigDecimal basePrice;
}
