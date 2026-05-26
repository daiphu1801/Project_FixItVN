package com.fixit.domain.service_categories.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceItemResponse {
    private Integer id;
    private String itemName;
    private BigDecimal suggestedPrice;
    private Integer serviceCategoryId;
}
