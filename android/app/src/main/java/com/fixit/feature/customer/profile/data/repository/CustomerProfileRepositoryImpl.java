package com.fixit.feature.customer.profile.data.repository;

import androidx.annotation.NonNull;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.profile.data.remote.api.CustomerProfileApi;
import com.fixit.feature.customer.profile.data.remote.dto.request.CustomerAddressRequestDto;
import com.fixit.feature.customer.profile.data.remote.dto.response.CustomerAddressResponseDto;
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
                    ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                    String errMsg = apiResponse != null ? apiResponse.getMessage() : "Mã lỗi " + response.code();
                    callback.onResult(Result.error(new AppError(errMsg)));
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
                    ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                    String errMsg = apiResponse != null ? apiResponse.getMessage() : "Mã lỗi " + response.code();
                    callback.onResult(Result.error(new AppError(errMsg)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerProfileResponseDto>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError("Rớt mạng khi đang lưu: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getAddresses(ResultCallback<List<CustomerAddress>> callback) {
        api.getAddresses().enqueue(new Callback<ApiResponse<List<CustomerAddressResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CustomerAddressResponseDto>>> call, @NonNull Response<ApiResponse<List<CustomerAddressResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CustomerAddress> result = CustomerProfileMapper.toAddressDomainList(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    String errMsg = "Mã lỗi " + response.code();
                    try { if (response.errorBody() != null) errMsg += ": " + response.errorBody().string(); } catch (Exception ignored) {}
                    callback.onResult(Result.error(new AppError("Lỗi tải danh sách địa chỉ (" + errMsg + ")")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CustomerAddressResponseDto>>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối mạng: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void addAddress(CustomerAddress address, ResultCallback<CustomerAddress> callback) {
        CustomerAddressRequestDto requestDto = new CustomerAddressRequestDto(
                address.getLabel(),
                address.getAddress(),
                address.getLatitude(),
                address.getLongitude(),
                address.getDefaultAddress()
        );
        api.addAddress(requestDto).enqueue(new Callback<ApiResponse<CustomerAddressResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomerAddressResponseDto>> call, @NonNull Response<ApiResponse<CustomerAddressResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerAddress result = CustomerProfileMapper.toDomain(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    String errMsg = "Mã lỗi " + response.code();
                    try { if (response.errorBody() != null) errMsg += ": " + response.errorBody().string(); } catch (Exception ignored) {}
                    callback.onResult(Result.error(new AppError("Không thể thêm địa chỉ (" + errMsg + ")")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerAddressResponseDto>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError("Rớt mạng khi thêm địa chỉ: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void updateAddress(String addressId, CustomerAddress address, ResultCallback<CustomerAddress> callback) {
        CustomerAddressRequestDto requestDto = new CustomerAddressRequestDto(
                address.getLabel(),
                address.getAddress(),
                address.getLatitude(),
                address.getLongitude(),
                address.getDefaultAddress()
        );
        api.updateAddress(addressId, requestDto).enqueue(new Callback<ApiResponse<CustomerAddressResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomerAddressResponseDto>> call, @NonNull Response<ApiResponse<CustomerAddressResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerAddress result = CustomerProfileMapper.toDomain(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    String errMsg = "Mã lỗi " + response.code();
                    try { if (response.errorBody() != null) errMsg += ": " + response.errorBody().string(); } catch (Exception ignored) {}
                    callback.onResult(Result.error(new AppError("Không thể cập nhật địa chỉ (" + errMsg + ")")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerAddressResponseDto>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError("Rớt mạng khi cập nhật địa chỉ: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void deleteAddress(String addressId, ResultCallback<Void> callback) {
        api.deleteAddress(addressId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    String errMsg = "Mã lỗi " + response.code();
                    try { if (response.errorBody() != null) errMsg += ": " + response.errorBody().string(); } catch (Exception ignored) {}
                    callback.onResult(Result.error(new AppError("Không thể xóa địa chỉ (" + errMsg + ")")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError("Rớt mạng khi xóa địa chỉ: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void setDefaultAddress(String addressId, ResultCallback<CustomerAddress> callback) {
        api.setDefaultAddress(addressId).enqueue(new Callback<ApiResponse<CustomerAddressResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomerAddressResponseDto>> call, @NonNull Response<ApiResponse<CustomerAddressResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerAddress result = CustomerProfileMapper.toDomain(response.body().getData());
                    callback.onResult(Result.success(result));
                } else {
                    String errMsg = "Mã lỗi " + response.code();
                    try { if (response.errorBody() != null) errMsg += ": " + response.errorBody().string(); } catch (Exception ignored) {}
                    callback.onResult(Result.error(new AppError("Không thể đặt địa chỉ mặc định (" + errMsg + ")")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerAddressResponseDto>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError("Rớt mạng khi đặt địa chỉ mặc định: " + t.getMessage(), t)));
            }
        });
    }
}
