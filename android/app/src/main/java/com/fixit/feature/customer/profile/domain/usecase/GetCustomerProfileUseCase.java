package com.fixit.feature.customer.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import javax.inject.Inject;

// CÚ PHÁP: public class [Tên_Class]
// Ý NGHĨA: Khai báo một UseCase (Lệnh nghiệp vụ). 
// Lệnh này nằm ở tầng VIP (Domain), nó đại diện cho duy nhất một hành động: "Lấy thông tin cá nhân".
public class GetCustomerProfileUseCase {

    // Nó cần gọi Bộ não (Repository) để sai vặt.
    private final CustomerProfileRepository repository;

    // CÚ PHÁP: @Inject
    // Ý NGHĨA: Nhờ Hilt tự động đưa cái Repository (File 12) vào đây.
    @Inject
    public GetCustomerProfileUseCase(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------
    // HÀM THỰC THI CHÍNH (execute)
    // ----------------------------------------------------
    // CÚ PHÁP: public void execute([Tham_số_nếu_có], ResultCallback<[Kiểu_Trả_Về]> callback)
    // Ý NGHĨA: Hàm này giống như một cái Nút Bấm. Màn hình (ViewModel) chỉ cần thò tay bấm cái nút này,
    // truyền cái bộ đàm (callback) vào, rồi UseCase sẽ tự động đi xuống kho (Repository) lôi hàng lên giao lại.
    public void execute(ResultCallback<CustomerProfile> callback) {
        // Chỉ việc gọi hàm getProfile() của cái kho
        repository.getProfile(callback);
    }
}
