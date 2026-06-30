package com.fixit.feature.customer.profile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.usecase.GetCustomerAddressesUseCase;
import com.fixit.feature.customer.profile.domain.usecase.AddCustomerAddressUseCase;
import com.fixit.feature.customer.profile.domain.usecase.UpdateCustomerAddressUseCase;
import com.fixit.feature.customer.profile.domain.usecase.DeleteCustomerAddressUseCase;
import com.fixit.feature.customer.profile.domain.usecase.SetDefaultCustomerAddressUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

// Trạm phát sóng truyền hình cho Sổ địa chỉ
@HiltViewModel
public class AddressViewModel extends ViewModel {

    private final GetCustomerAddressesUseCase getAddressesUseCase;
    private final AddCustomerAddressUseCase addAddressUseCase;
    private final UpdateCustomerAddressUseCase updateAddressUseCase;
    private final DeleteCustomerAddressUseCase deleteAddressUseCase;
    private final SetDefaultCustomerAddressUseCase setDefaultAddressUseCase;

    // 3 Kênh phát sóng quen thuộc
    private final MutableLiveData<List<CustomerAddress>> addressesData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    @Inject
    public AddressViewModel(GetCustomerAddressesUseCase getAddressesUseCase,
                            AddCustomerAddressUseCase addAddressUseCase,
                            UpdateCustomerAddressUseCase updateAddressUseCase,
                            DeleteCustomerAddressUseCase deleteAddressUseCase,
                            SetDefaultCustomerAddressUseCase setDefaultAddressUseCase) {
        this.getAddressesUseCase = getAddressesUseCase;
        this.addAddressUseCase = addAddressUseCase;
        this.updateAddressUseCase = updateAddressUseCase;
        this.deleteAddressUseCase = deleteAddressUseCase;
        this.setDefaultAddressUseCase = setDefaultAddressUseCase;
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

    public void addAddress(CustomerAddress address) {
        isLoading.setValue(true);
        addAddressUseCase.execute(address, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                successMessage.postValue("Thêm địa chỉ thành công");
                loadAddresses();
            } else {
                errorMessage.postValue(result.getError().getMessage());
            }
        });
    }

    public void updateAddress(String addressId, CustomerAddress address) {
        isLoading.setValue(true);
        updateAddressUseCase.execute(addressId, address, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                successMessage.postValue("Cập nhật địa chỉ thành công");
                loadAddresses();
            } else {
                errorMessage.postValue(result.getError().getMessage());
            }
        });
    }

    public void deleteAddress(String addressId) {
        isLoading.setValue(true);
        deleteAddressUseCase.execute(addressId, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                successMessage.postValue("Xóa địa chỉ thành công");
                loadAddresses();
            } else {
                errorMessage.postValue(result.getError().getMessage());
            }
        });
    }

    public void setDefaultAddress(String addressId) {
        isLoading.setValue(true);
        setDefaultAddressUseCase.execute(addressId, result -> {
            isLoading.postValue(false);
            if (result.isSuccess()) {
                successMessage.postValue("Đã đặt làm địa chỉ mặc định");
                loadAddresses();
            } else {
                errorMessage.postValue(result.getError().getMessage());
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

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
}
