package com.fixit.feature.customer.profile.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;

import java.util.List;

// CÚ PHÁP: public interface [Tên_Interface]
// Ý NGHĨA: Đây là Bản hợp đồng (Interface) nằm ở tầng Cốt lõi (Domain).
// Bản hợp đồng này KHÔNG thèm quan tâm đến Retrofit, GSON hay mạng Internet.
// Nó chỉ liệt kê các hành động: "Tôi cần lấy Profile", "Tôi cần thêm Địa chỉ".
// Tầng Data (RepositoryImpl) sẽ phải thực thi hợp đồng này.
public interface CustomerProfileRepository {

    // ----------------------------------------------------
    // NHÓM 1: PROFILE
    // ----------------------------------------------------

    // Ý NGHĨA: Hành động "Lấy Profile". 
    // ResultCallback<CustomerProfile>: Vì Android gọi mạng bị trễ (mất vài giây), 
    // nên ta không thể return ngay lập tức được. Ta phải truyền một cái "Bộ đàm" (Callback) vào.
    // Khi nào có dữ liệu xong, hệ thống sẽ tự động bóp bộ đàm gọi lại cho ta.
    // LƯU Ý: Ở tầng này, ta trả về dữ liệu thật (CustomerProfile) chứ KHÔNG PHẢI vỏ hộp (CustomerProfileResponseDto).
    void getProfile(ResultCallback<CustomerProfile> callback);

    void updateProfile(String fullName, String email, String gender, String dob, ResultCallback<CustomerProfile> callback);

    // ----------------------------------------------------
    // NHÓM 2: ADDRESS
    // ----------------------------------------------------

    void getAddresses(ResultCallback<List<CustomerAddress>> callback);

    void addAddress(CustomerAddress address, ResultCallback<CustomerAddress> callback);

    void updateAddress(String addressId, CustomerAddress address, ResultCallback<CustomerAddress> callback);

    void deleteAddress(String addressId, ResultCallback<Void> callback);

    void setDefaultAddress(String addressId, ResultCallback<CustomerAddress> callback);
}
