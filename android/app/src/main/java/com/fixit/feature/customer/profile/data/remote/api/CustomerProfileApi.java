package com.fixit.feature.customer.profile.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.profile.data.remote.dto.request.CustomerAddressRequestDto;
import com.fixit.feature.customer.profile.data.remote.dto.request.CustomerProfileRequestDto;
import com.fixit.feature.customer.profile.data.remote.dto.response.CustomerAddressResponseDto;
import com.fixit.feature.customer.profile.data.remote.dto.response.CustomerProfileResponseDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

// CÚ PHÁP: public interface [Tên_Interface]
// Ý NGHĨA: Khai báo bản đồ API (Ống nghe điện thoại Retrofit). 
// Thư viện Retrofit sẽ đọc file này và tự động viết code gửi tín hiệu mạng Internet lên Backend.
public interface CustomerProfileApi {

    // ==========================================
    // NHÓM 1: PROFILE
    // ==========================================

    // CÚ PHÁP: @GET("[Đường_dẫn_API]")
    // Ý NGHĨA: Bấm nút "Gọi" lên đường dẫn này để lấy dữ liệu. Đường dẫn phải khớp y hệt File 8 (Controller) bên Backend.
    @GET("api/v1/customers/me/profile")
    
    // CÚ PHÁP: Call<[Kiểu_Trả_Về]> [Tên_Hàm]();
    // Ý NGHĨA: Call là cuộc gọi mạng của Retrofit. ApiResponse là vỏ hộp bọc ngoài cùng (chứa mã lỗi). CustomerProfileResponseDto là thùng xốp chứa dữ liệu thực.
    Call<ApiResponse<CustomerProfileResponseDto>> getProfile();

    // Ý NGHĨA: Bấm nút gọi bằng phương thức PUT để sửa thông tin.
    @PUT("api/v1/customers/me/profile")
    Call<ApiResponse<CustomerProfileResponseDto>> updateProfile(
        // CÚ PHÁP: @Body [Kiểu_Dữ_Liệu] [Tên_Biến]
        // Ý NGHĨA: Lấy cái thùng xốp DTO ở điện thoại, nén thành chuỗi JSON rồi nhét vào bụng (Body) gói tin bắn lên trời.
        @Body CustomerProfileRequestDto requestDto
    );

    // ==========================================
    // NHÓM 2: ADDRESS
    // ==========================================

    @GET("api/v1/customers/me/addresses")
    Call<ApiResponse<List<CustomerAddressResponseDto>>> getAddresses();

    @POST("api/v1/customers/me/addresses")
    Call<ApiResponse<CustomerAddressResponseDto>> addAddress(
        @Body CustomerAddressRequestDto requestDto
    );

    // Chữ {addressId} là một khoảng trống để điền số vào.
    @PUT("api/v1/customers/me/addresses/{addressId}")
    Call<ApiResponse<CustomerAddressResponseDto>> updateAddress(
        // CÚ PHÁP: @Path("[Tên_Biến_Trên_URL]")
        // Ý NGHĨA: Gắp biến String addressId này nhét thay thế vào chỗ trống {addressId} trên đường dẫn phía trên.
        @Path("addressId") String addressId,
        @Body CustomerAddressRequestDto requestDto
    );

    @DELETE("api/v1/customers/me/addresses/{addressId}")
    Call<ApiResponse<Void>> deleteAddress(
        @Path("addressId") String addressId
    );

    @PATCH("api/v1/customers/me/addresses/{addressId}/default")
    Call<ApiResponse<CustomerAddressResponseDto>> setDefaultAddress(
        @Path("addressId") String addressId
    );
}
