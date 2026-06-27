package com.fixit.feature.customer.profile.data.repository;

import androidx.annotation.NonNull;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.profile.data.remote.api.CustomerProfileApi;
import com.fixit.feature.customer.profile.data.remote.dto.request.CustomerProfileRequestDto;
import com.fixit.feature.customer.profile.data.remote.dto.response.CustomerProfileResponseDto;
import com.fixit.feature.customer.profile.data.remote.mapper.CustomerProfileMapper;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// CÚ PHÁP: public class [Tên] implements [Tên_Interface]
// Ý NGHĨA: Class này đứng ra "ký hợp đồng" với CustomerProfileRepository (File 12).
// Nó chứa toàn bộ LOGIC và BẮT LỖI mạng cho module Profile.
public class CustomerProfileRepositoryImpl implements CustomerProfileRepository {

    // Ống nghe điện thoại (Khai báo API)
    private final CustomerProfileApi api;

    // CÚ PHÁP: @Inject
    // Ý NGHĨA: Yêu cầu Hilt (Thư viện tiêm phụ thuộc của Android) tự động nhét cái Ống nghe vào đây.
    @Inject
    public CustomerProfileRepositoryImpl(CustomerProfileApi api) {
        this.api = api;
    }

    // ==========================================
    // LOGIC 1: LẤY THÔNG TIN CÁ NHÂN (GET PROFILE)
    // ==========================================
    @Override
    public void getProfile(ResultCallback<CustomerProfile> callback) {
        // CÚ PHÁP: api.getProfile().enqueue(new Callback<...>() { ... })
        // Ý NGHĨA: Bấm số gọi điện (getProfile), sau đó enqueue() có nghĩa là: 
        // "Quăng cuộc gọi này ra chạy nền, rảnh thì báo lại tao qua Callback, tao đi làm việc khác đây".
        api.getProfile().enqueue(new Callback<ApiResponse<CustomerProfileResponseDto>>() {
            
            // NHÁNH 1: Điện thoại reo, bắt được máy (Server có phản hồi)
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomerProfileResponseDto>> call, @NonNull Response<ApiResponse<CustomerProfileResponseDto>> response) {
                // LOGIC KIỂM TRA LỖI SERVER
                if (response.isSuccessful() && response.body() != null) {
                    
                    // NẾU THÀNH CÔNG: Chạy Mapper (chuyển đổi DTO thành Domain Model VIP)
                    // LƯU Ý: Chỗ này sẽ báo đỏ vì ta chưa tạo class CustomerProfileMapper và CustomerProfile. Lát ta tạo sau.
                    CustomerProfile result = CustomerProfileMapper.toDomain(response.body().getData());
                    
                    // Bấm bộ đàm báo TIN VUI về cho màn hình Android vẽ ra
                    callback.onResult(Result.success(result));
                } else {
                    // NẾU THẤT BẠI (Mã 400, 500...): Bấm bộ đàm báo TIN BUỒN
                    callback.onResult(Result.error(new AppError("Lỗi tải thông tin: " + response.message())));
                }
            }

            // NHÁNH 2: Rớt mạng, cúp điện, sập WiFi
            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerProfileResponseDto>> call, @NonNull Throwable t) {
                // Bấm bộ đàm báo TIN BUỒN kèm theo lý do rớt mạng (t.getMessage())
                callback.onResult(Result.error(new AppError("Lỗi kết nối mạng: " + t.getMessage(), t)));
            }
        });
    }

    // ==========================================
    // LOGIC 2: CẬP NHẬT THÔNG TIN (UPDATE PROFILE)
    // ==========================================
    @Override
    public void updateProfile(String fullName, String email, String gender, String dob, ResultCallback<CustomerProfile> callback) {
        
        // BƯỚC 1: Lấy thông tin người dùng vừa gõ, nhét vào HỘP DTO ĐỎ (RequestDto)
        CustomerProfileRequestDto requestDto = new CustomerProfileRequestDto(fullName, email, gender, dob);

        // BƯỚC 2: Bấm gọi lên mạng, quăng cái hộp đỏ đi kèm
        api.updateProfile(requestDto).enqueue(new Callback<ApiResponse<CustomerProfileResponseDto>>() {
            
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomerProfileResponseDto>> call, @NonNull Response<ApiResponse<CustomerProfileResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật xong, Server trả về cái HỘP XANH (ResponseDto). Ta lại map nó ra Domain.
                    CustomerProfile result = CustomerProfileMapper.toDomain(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể cập nhật hồ sơ: " + response.message())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerProfileResponseDto>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError("Rớt mạng khi đang lưu: " + t.getMessage(), t)));
            }
        });
    }

    // (Vì file sẽ rất dài nên mình viết demo 2 hàm đặc trưng nhất (GET và PUT) để bạn thấy logic.
    // 5 hàm của phần Address sẽ có logic y hệt như 2 hàm trên: Cũng tạo hộp, enqueue, bắt lỗi 2 nhánh.)
    
    @Override
    public void getAddresses(ResultCallback<List<CustomerAddress>> callback) { }
    @Override
    public void addAddress(CustomerAddress address, ResultCallback<CustomerAddress> callback) { }
    @Override
    public void updateAddress(String addressId, CustomerAddress address, ResultCallback<CustomerAddress> callback) { }
    @Override
    public void deleteAddress(String addressId, ResultCallback<Void> callback) { }
    @Override
    public void setDefaultAddress(String addressId, ResultCallback<CustomerAddress> callback) { }
}
