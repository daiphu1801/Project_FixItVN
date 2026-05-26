package com.fixit.domain.service_categories.service;

import java.util.List;
import com.fixit.domain.service_categories.dto.response.ServiceCategoryResponse;
import com.fixit.domain.service_categories.dto.response.ServiceItemResponse;

public interface ServiceCategoryService {
    List<ServiceCategoryResponse> getAllCategories();
    ServiceCategoryResponse getCategoryById(Integer id);
    List<ServiceItemResponse> getItemsByCategoryId(Integer categoryId);
}
