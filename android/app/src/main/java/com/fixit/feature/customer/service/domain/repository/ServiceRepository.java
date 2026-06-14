package com.fixit.feature.customer.service.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.model.ServiceItem;

import java.util.List;

public interface ServiceRepository {
    void getAllCategories(ResultCallback<List<ServiceCategory>> callback);
    void getCategoryById(Integer id, ResultCallback<ServiceCategory> callback);
    void getItemsByCategoryId(Integer categoryId, ResultCallback<List<ServiceItem>> callback);
}
