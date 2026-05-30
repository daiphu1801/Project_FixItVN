package com.fixit.domain.service_categories.controller;

import com.fixit.domain.service_categories.dto.response.ServiceCategoryResponse;
import com.fixit.domain.service_categories.dto.response.ServiceItemResponse;
import com.fixit.domain.service_categories.service.ServiceCategoryService;
import com.fixit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services/categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;

    @GetMapping
    public ApiResponse<List<ServiceCategoryResponse>> getAllCategories() {
        return ApiResponse.success(serviceCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceCategoryResponse> getCategoryById(@PathVariable Integer id) {
        return ApiResponse.success(serviceCategoryService.getCategoryById(id));
    }

    @GetMapping("/{id}/items")
    public ApiResponse<List<ServiceItemResponse>> getItemsByCategoryId(@PathVariable Integer id) {
        return ApiResponse.success(serviceCategoryService.getItemsByCategoryId(id));
    }
}
