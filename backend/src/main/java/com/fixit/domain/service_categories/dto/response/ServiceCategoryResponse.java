package com.fixit.domain.service_categories.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceCategoryResponse {
    private Integer id;
    private String serviceName;
}
