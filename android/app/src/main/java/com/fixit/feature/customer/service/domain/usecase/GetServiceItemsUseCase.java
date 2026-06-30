package com.fixit.feature.customer.service.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.service.domain.model.ServiceItem;
import com.fixit.feature.customer.service.domain.repository.ServiceRepository;

import java.util.List;

import javax.inject.Inject;

public class GetServiceItemsUseCase {

    private final ServiceRepository repository;

    @Inject
    public GetServiceItemsUseCase(ServiceRepository repository) {
        this.repository = repository;
    }

    public void execute(Integer categoryId, ResultCallback<List<ServiceItem>> callback) {
        repository.getItemsByCategoryId(categoryId, callback);
    }
}
