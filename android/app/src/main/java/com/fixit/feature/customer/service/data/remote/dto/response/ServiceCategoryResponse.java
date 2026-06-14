package com.fixit.feature.customer.service.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * DTO (Data Transfer Object) - Phía Android
 * =========================================
 * Khi app Android gọi Backend, Backend sẽ trả về một chuỗi JSON như sau:
 * {
 *   "id": 1,
 *   "serviceName": "Sửa điện nước"
 * }
 * 
 * Thư viện Retrofit/GSON trên Android cần một "Cái khuôn" (Class này) 
 * để đổ dữ liệu JSON đó vào thành một Object Java.
 */
public class ServiceCategoryResponse {
    
    // @SerializedName("id") báo cho GSON biết: 
    // "Hãy lấy giá trị của chữ 'id' trong JSON nhét vào biến id này nhé"
    @SerializedName("id")
    private Integer id;

    // "Hãy lấy giá trị của chữ 'serviceName' trong JSON nhét vào biến serviceName này nhé"
    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("iconUrl")
    private String iconUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
