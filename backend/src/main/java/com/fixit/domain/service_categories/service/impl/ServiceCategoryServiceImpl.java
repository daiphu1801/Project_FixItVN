package com.fixit.domain.service_categories.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fixit.domain.service_categories.dto.response.ServiceCategoryResponse;
import com.fixit.domain.service_categories.dto.response.ServiceItemResponse;
import com.fixit.domain.service_categories.entity.ServiceCategory;
import com.fixit.domain.service_categories.entity.ServiceItem;
import com.fixit.domain.service_categories.repository.ServiceCategoryRepository;
import com.fixit.domain.service_categories.repository.ServiceItemRepository;
import com.fixit.domain.service_categories.service.ServiceCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository categoryRepository;
    private final ServiceItemRepository itemRepository;

    @Override
    public List<ServiceCategoryResponse> getAllCategories() {
        List<ServiceCategory> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> ServiceCategoryResponse.builder()
                        .id(category.getId())
                        .serviceName(category.getServiceName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ServiceCategoryResponse getCategoryById(Integer id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục dịch vụ với ID: " + id));

        return ServiceCategoryResponse.builder()
                .id(category.getId())
                .serviceName(category.getServiceName())
                .build();
    }

    @Override
    public List<ServiceItemResponse> getItemsByCategoryId(Integer categoryId) {
        List<ServiceItem> items = itemRepository.findByServiceCategoryId(categoryId);

        return items.stream()
                .map(item -> ServiceItemResponse.builder()
                        .id(item.getId())
                        .serviceCategoryId(item.getServiceCategory().getId())
                        .itemName(item.getItemName())
                        .suggestedPrice(item.getSuggestedPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
