package com.fixit.domain.customer.service;

import com.fixit.domain.customer.dto.response.FavoriteWorkerResponse;
import java.util.List;
import java.util.UUID;

public interface FavoriteWorkerService {
    void addFavorite(UUID workerId);

    void removeFavorite(UUID workerId);

    boolean isFavorite(UUID workerId);

    List<FavoriteWorkerResponse> getFavorites();
}
