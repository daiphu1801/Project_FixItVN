package com.fixit.feature.customer.favorite.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.favorite.domain.repository.FavoriteRepository;

import javax.inject.Inject;

public class CheckFavoriteStatusUseCase {
    private final FavoriteRepository repository;

    @Inject
    public CheckFavoriteStatusUseCase(FavoriteRepository repository) {
        this.repository = repository;
    }

    public void execute(String workerId, ResultCallback<Boolean> callback) {
        repository.isFavorite(workerId, callback);
    }
}
