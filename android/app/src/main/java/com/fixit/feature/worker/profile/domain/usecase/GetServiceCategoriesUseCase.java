package com.fixit.feature.worker.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.profile.domain.model.ServiceCategory;
import com.fixit.feature.worker.profile.domain.repository.WorkerProfileRepository;

import java.util.List;

import javax.inject.Inject;

public class GetServiceCategoriesUseCase {

    private final WorkerProfileRepository repository;

    @Inject
    public GetServiceCategoriesUseCase(WorkerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<ServiceCategory>> callback) {
        repository.getServiceCategories(callback);
    }
}
