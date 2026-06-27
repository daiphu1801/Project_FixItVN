package com.fixit.feature.customer.profile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;
import com.fixit.feature.customer.profile.domain.usecase.GetCustomerProfileUseCase;
import com.fixit.feature.customer.profile.domain.usecase.UpdateCustomerProfileUseCase;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CustomerProfileViewModel extends ViewModel {

    private final GetCustomerProfileUseCase getCustomerProfileUseCase;
    private final UpdateCustomerProfileUseCase updateCustomerProfileUseCase;

    private final MutableLiveData<CustomerProfile> profileData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    @Inject
    public CustomerProfileViewModel(
            GetCustomerProfileUseCase getCustomerProfileUseCase,
            UpdateCustomerProfileUseCase updateCustomerProfileUseCase
    ) {
        this.getCustomerProfileUseCase = getCustomerProfileUseCase;
        this.updateCustomerProfileUseCase = updateCustomerProfileUseCase;
    }

    public void loadProfile() {
        isLoading.setValue(true); 
        getCustomerProfileUseCase.execute(new ResultCallback<CustomerProfile>() {
            @Override
            public void onResult(Result<CustomerProfile> result) {
                isLoading.postValue(false); 
                if (result.isSuccess()) {
                    profileData.postValue(result.getData()); 
                } else {
                    errorMessage.postValue(result.getError().getMessage()); 
                }
            }
        });
    }

    public void updateProfile(String fullName, String email, String gender, String dob) {
        isLoading.setValue(true);
        updateSuccess.setValue(null);
        updateCustomerProfileUseCase.execute(fullName, email, gender, dob, new ResultCallback<CustomerProfile>() {
            @Override
            public void onResult(Result<CustomerProfile> result) {
                isLoading.postValue(false);
                if (result.isSuccess()) {
                    profileData.postValue(result.getData());
                    updateSuccess.postValue(true);
                } else {
                    errorMessage.postValue(result.getError().getMessage());
                    updateSuccess.postValue(false);
                }
            }
        });
    }

    public void clearUpdateSuccess() {
        updateSuccess.setValue(null);
    }

    // ----------------------------------------------------
    // CHỖ ĐỂ TIVI (FRAGMENT) CẮM ĂNG-TEN VÀO BẮT SÓNG
    // ----------------------------------------------------
    // Khúc này trả về LiveData (Không có chữ Mutable).
    // Có nghĩa là Tivi (Fragment) chỉ được phép xem phim, KHÔNG ĐƯỢC PHÉP nhảy vào sửa đổi nội dung kênh truyền hình.
    
    public LiveData<CustomerProfile> getProfileData() {
        return profileData;
    }

    public LiveData<CustomerProfile> getProfileData() { return profileData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getUpdateSuccess() { return updateSuccess; }
    public LiveData<Boolean> getLogoutSuccess() { return logoutSuccess; }

    public void logout() {
        isLoading.setValue(true);
        logoutUseCase.execute(new ResultCallback<Void>() {
            @Override
            public void onResult(Result<Void> result) {
                isLoading.postValue(false);
                if (result.isSuccess()) {
                    logoutSuccess.postValue(true);
                } else {
                    errorMessage.postValue(result.getError().getMessage());
                }
            }
        });
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }
}
