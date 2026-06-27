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

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Báo cho Android biết đây là một Trạm Phát Sóng (ViewModel) được cấp phép hoạt động.
@HiltViewModel

// CÚ PHÁP: public class [Tên_Class] extends ViewModel
// Ý NGHĨA: ViewModel là cầu nối duy nhất giữa Logic (UseCase) và Giao diện (Fragment).
public class CustomerProfileViewModel extends ViewModel {

    // ViewModel xài cái nút bấm UseCase (Thay vì xài trực tiếp Repository)
    private final GetCustomerProfileUseCase getCustomerProfileUseCase;
    private final UpdateCustomerProfileUseCase updateCustomerProfileUseCase;

    // ----------------------------------------------------
    // CÁC KÊNH PHÁT SÓNG (LiveData)
    // ----------------------------------------------------
    // Ở đây ta có 3 kênh truyền hình. Chữ "Mutable" nghĩa là Trạm phát sóng có quyền thay đổi nội dung chương trình.
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

    // ----------------------------------------------------
    // HÀNH ĐỘNG CỦA NGƯỜI DÙNG (Ví dụ: Kéo màn hình để tải lại)
    // ----------------------------------------------------
    public void loadProfile() {
        
        // Phát sóng lên Kênh 2: "Đang tải dữ liệu đấy, bật cái vòng tròn xoay xoay lên đi!"
        isLoading.setValue(true); 
        
        // Bấm nút UseCase để đi lấy hàng
        getCustomerProfileUseCase.execute(new ResultCallback<CustomerProfile>() {
            @Override
            public void onResult(Result<CustomerProfile> result) {
                
                // Hàng về rồi! Phát sóng Kênh 2: "Tắt cái vòng tròn xoay xoay đi!"
                isLoading.postValue(false); 
                
                if (result.isSuccess()) {
                    // Nếu thành công: Phát sóng Kênh 1: Cầm cục dữ liệu VIP (CustomerProfile) phát đi cho màn hình bắt sóng.
                    profileData.postValue(result.getData()); 
                } else {
                    // Nếu lỗi (cúp điện, rớt mạng): Phát sóng Kênh 3: Báo dòng chữ đỏ "Lỗi mạng" lên màn hình.
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

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }
}
