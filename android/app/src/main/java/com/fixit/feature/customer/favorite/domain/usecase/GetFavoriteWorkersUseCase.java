package com.fixit.feature.customer.favorite.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.favorite.domain.model.FavoriteWorker;
import com.fixit.feature.customer.favorite.domain.repository.FavoriteRepository;

import java.util.List;
import javax.inject.Inject;

public class GetFavoriteWorkersUseCase {
    private final FavoriteRepository repository;

    @Inject
    public GetFavoriteWorkersUseCase(FavoriteRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<FavoriteWorker>> callback) {
        repository.getFavorites(callback);
    }
}
