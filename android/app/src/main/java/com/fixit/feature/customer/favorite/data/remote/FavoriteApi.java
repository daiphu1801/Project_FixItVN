package com.fixit.feature.customer.favorite.data.remote;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.favorite.domain.model.FavoriteWorker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FavoriteApi {
    @POST("api/v1/customers/favorites/{workerId}")
    Call<ApiResponse<Void>> addFavorite(@Path("workerId") String workerId);

    @DELETE("api/v1/customers/favorites/{workerId}")
    Call<ApiResponse<Void>> removeFavorite(@Path("workerId") String workerId);

    @GET("api/v1/customers/favorites/{workerId}/status")
    Call<ApiResponse<Boolean>> isFavorite(@Path("workerId") String workerId);

    @GET("api/v1/customers/favorites")
    Call<ApiResponse<List<FavoriteWorker>>> getFavorites();
}
