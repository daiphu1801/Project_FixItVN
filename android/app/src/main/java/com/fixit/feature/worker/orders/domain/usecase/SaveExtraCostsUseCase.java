package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import java.util.List;

import javax.inject.Inject;

public class SaveExtraCostsUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public SaveExtraCostsUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public void execute(List<ExtraCostItem> items) {
        repository.saveExtraCosts(items);
    }
}
