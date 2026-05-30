package com.fixit.feature.customer.service.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.service.data.remote.dto.response.ServiceCategoryResponse;
import com.fixit.feature.customer.service.data.remote.dto.response.ServiceItemResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * FILE SỐ 1: ServiceApi (Ống nghe điện thoại Retrofit)
 * ====================================================
 * Chức năng cốt lõi: 
 * Nơi khai báo các "Đầu số điện thoại" (Đường link API) để App Android 
 * có thể gọi điện lên Backend xin dữ liệu.
 * 
 * Cách hoạt động:
 * Thư viện Retrofit sẽ đọc file này, và tự động biến các hàm Java bên dưới 
 * thành những cục sóng Wifi (gói tin HTTP) bắn lên mạng Internet.
 */
public interface ServiceApi {

    /**
     * Cú pháp @GET("api/v1/services/categories") nghĩa là:
     * "Khi tôi gọi hàm getAllCategories(), hãy gửi yêu cầu lấy dữ liệu (GET) 
     * tới địa chỉ IP của Backend nối với cái đuôi 'api/v1/services/categories' "
     * 
     * Nó khớp CHÍNH XÁC 100% với file ServiceCategoryController bên Backend!
     */
    @GET("api/v1/services/categories")
    Call<ApiResponse<List<ServiceCategoryResponse>>> getAllCategories();

    /**
     * @Path("id"): Dùng để bóc cái chữ 'id' truyền vào hàm, 
     * nhét lên thanh địa chỉ URL. 
     * VD: truyền id = 1 -> URL sẽ là 'api/v1/services/categories/1'
     */
    @GET("api/v1/services/categories/{id}")
    Call<ApiResponse<ServiceCategoryResponse>> getCategoryById(@Path("id") Integer id);

    /**
     * Lấy danh sách dịch vụ con của một nhóm cha.
     */
    @GET("api/v1/services/categories/{categoryId}/items")
    Call<ApiResponse<List<ServiceItemResponse>>> getItemsByCategoryId(@Path("categoryId") Integer categoryId);
}
