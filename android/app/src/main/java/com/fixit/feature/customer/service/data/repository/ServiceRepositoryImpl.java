package com.fixit.feature.customer.service.data.repository;

import androidx.annotation.NonNull;

import com.fixit.core.network.ApiResponse;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.common.Result;
import com.fixit.core.common.AppError;
import com.fixit.feature.customer.service.data.remote.api.ServiceApi;
import com.fixit.feature.customer.service.data.remote.dto.response.ServiceCategoryResponse;
import com.fixit.feature.customer.service.data.remote.dto.response.ServiceItemResponse;
import com.fixit.feature.customer.service.data.remote.mapper.ServiceMapper;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.model.ServiceItem;
import com.fixit.feature.customer.service.domain.repository.ServiceRepository;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceRepositoryImpl implements ServiceRepository {

    private final ServiceApi api;

    @Inject
    public ServiceRepositoryImpl(ServiceApi api) {
        this.api = api;
    }

    @Override
    public void getAllCategories(ResultCallback<List<ServiceCategory>> callback) {
        api.getAllCategories().enqueue(new Callback<ApiResponse<List<ServiceCategoryResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ServiceCategoryResponse>>> call, @NonNull Response<ApiResponse<List<ServiceCategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ServiceCategory> result = ServiceMapper.toCategoryDomainList(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    callback.onResult(Result.error(new AppError("Failed to load categories: " + response.message())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ServiceCategoryResponse>>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getCategoryById(Integer id, ResultCallback<ServiceCategory> callback) {
        api.getCategoryById(id).enqueue(new Callback<ApiResponse<ServiceCategoryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ServiceCategoryResponse>> call, @NonNull Response<ApiResponse<ServiceCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServiceCategory result = ServiceMapper.toDomain(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    callback.onResult(Result.error(new AppError("Failed to load category: " + response.message())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ServiceCategoryResponse>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getItemsByCategoryId(Integer categoryId, ResultCallback<List<ServiceItem>> callback) {
        api.getItemsByCategoryId(categoryId).enqueue(new Callback<ApiResponse<List<ServiceItemResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ServiceItemResponse>>> call, @NonNull Response<ApiResponse<List<ServiceItemResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ServiceItem> result = ServiceMapper.toItemDomainList(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    callback.onResult(Result.error(new AppError("Failed to load items: " + response.message())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ServiceItemResponse>>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }
}
