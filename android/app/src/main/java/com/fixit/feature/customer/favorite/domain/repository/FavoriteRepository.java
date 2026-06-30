
package com.fixit.feature.customer.favorite.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.favorite.domain.model.FavoriteWorker;

import java.util.List;

public interface FavoriteRepository {
    void addFavorite(String workerId, ResultCallback<Void> callback);

    void removeFavorite(String workerId, ResultCallback<Void> callback);

    void isFavorite(String workerId, ResultCallback<Boolean> callback);

    void getFavorites(ResultCallback<List<FavoriteWorker>> callback);
}
