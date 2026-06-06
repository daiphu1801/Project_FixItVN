package com.fixit.feature.customer.profile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.usecase.GetCustomerAddressesUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

// Trạm phát sóng truyền hình cho Sổ địa chỉ
@HiltViewModel
public class AddressViewModel extends ViewModel {

    private final GetCustomerAddressesUseCase getAddressesUseCase;

    // 3 Kênh phát sóng quen thuộc
    private final MutableLiveData<List<CustomerAddress>> addressesData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public AddressViewModel(GetCustomerAddressesUseCase getAddressesUseCase) {
        this.getAddressesUseCase = getAddressesUseCase;
    }

    // Hành động: Vuốt kéo tải danh sách địa chỉ
    public void loadAddresses() {
        isLoading.setValue(true); // Bật vòng xoay
        
        getAddressesUseCase.execute(new ResultCallback<List<CustomerAddress>>() {
            @Override
            public void onResult(Result<List<CustomerAddress>> result) {
                isLoading.postValue(false); // Tắt vòng xoay
                
                if (result.isSuccess()) {
                    // Mạng mượt, dữ liệu về, đẩy lên Kênh 1
                    addressesData.postValue(result.getData());
                } else {
                    // Rớt mạng, báo lỗi lên Kênh 3
                    errorMessage.postValue(result.getError().getMessage());
                }
            }
        });
    }

    // Cho phép Tivi cắm ăng-ten
    public LiveData<List<CustomerAddress>> getAddressesData() {
        return addressesData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
