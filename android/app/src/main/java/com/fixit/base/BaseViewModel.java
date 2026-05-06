package com.fixit.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel {

    // Trạng thái loading chung (dùng cho tất cả màn hình)
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // Thông báo lỗi chung (dùng cho tất cả màn hình)
    public final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    protected void setLoading(boolean loading) {
        isLoading.postValue(loading);
    }

    protected void setError(String message) {
        errorMessage.postValue(message);
    }
}
