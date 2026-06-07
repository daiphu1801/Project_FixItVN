package com.fixit.feature.customer.favorite.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.favorite.domain.repository.FavoriteRepository;

import javax.inject.Inject;

public class RemoveFavoriteWorkerUseCase {
    private final FavoriteRepository repository;

    @Inject
    public RemoveFavoriteWorkerUseCase(FavoriteRepository repository) {
        this.repository = repository;
    }

    public void execute(String workerId, ResultCallback<Void> callback) {
        repository.removeFavorite(workerId, callback);
    }
}
