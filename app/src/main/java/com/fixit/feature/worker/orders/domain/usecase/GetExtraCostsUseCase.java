package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import java.util.List;

import javax.inject.Inject;

public class GetExtraCostsUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public GetExtraCostsUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public List<ExtraCostItem> execute() {
        return repository.getExtraCosts();
    }
}
