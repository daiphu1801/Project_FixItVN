package com.fixit.feature.customer.favorite.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.favorite.data.remote.FavoriteApi;
import com.fixit.feature.customer.favorite.domain.model.FavoriteWorker;
import com.fixit.feature.customer.favorite.domain.repository.FavoriteRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final FavoriteApi favoriteApi;

    @Inject
    public FavoriteRepositoryImpl(FavoriteApi favoriteApi) {
        this.favoriteApi = favoriteApi;
    }

    @Override
    public void addFavorite(String workerId, ResultCallback<Void> callback) {
        favoriteApi.addFavorite(workerId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể thêm thợ yêu thích")));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void removeFavorite(String workerId, ResultCallback<Void> callback) {
        favoriteApi.removeFavorite(workerId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể xóa thợ yêu thích")));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void isFavorite(String workerId, ResultCallback<Boolean> callback) {
        favoriteApi.isFavorite(workerId).enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(response.body().getData()));
                } else {
                    callback.onResult(Result.success(false));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getFavorites(ResultCallback<List<FavoriteWorker>> callback) {
        favoriteApi.getFavorites().enqueue(new Callback<ApiResponse<List<FavoriteWorker>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FavoriteWorker>>> call,
                    Response<ApiResponse<List<FavoriteWorker>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(response.body().getData()));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể tải danh sách thợ quen")));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FavoriteWorker>>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }
}
