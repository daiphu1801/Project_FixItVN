package com.fixit.feature.customer.service.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.repository.ServiceRepository;

import java.util.List;

import javax.inject.Inject;

public class GetAllServiceCategoriesUseCase {

    private final ServiceRepository repository;

    @Inject
    public GetAllServiceCategoriesUseCase(ServiceRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<ServiceCategory>> callback) {
        repository.getAllCategories(callback);
    }
}
