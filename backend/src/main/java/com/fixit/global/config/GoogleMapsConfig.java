package com.fixit.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Cấu hình liên quan đến việc giao tiếp với các API của Google (cụ thể là Google Maps).
 * 
 * @Configuration: Báo cho Spring Boot biết đây là một class cấu hình, 
 * nó sẽ chạy khi khởi động ứng dụng để chuẩn bị sẵn các "đồ nghề" (Bean).
 */
@Configuration
public class GoogleMapsConfig {

    /**
     * Dùng @Value để lấy giá trị "app.google.maps.api-key" từ file application-dev.yml.
     * Nếu trong yml không có, chương trình sẽ báo lỗi lúc khởi động.
     */
    @Value("${app.google.maps.api-key}")
    private String googleMapsApiKey;

    /**
     * Tạo một RestTemplate - công cụ chuyên dùng để gọi API bên ngoài.
     * @Bean: Đánh dấu để Spring Boot lưu công cụ này vào bộ nhớ,
     * các class khác chỉ cần gọi ra dùng chung.
     */
    @Bean
    public RestTemplate googleMapsRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Cấu hình Timeout (thời gian chờ tối đa) khi gọi Google Maps
        // Tránh việc Google phản hồi chậm làm "treo" hệ thống của chúng ta.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 giây để kết nối
        factory.setReadTimeout(5000);    // 5 giây để nhận dữ liệu
        
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    /**
     * Cung cấp API Key cho các class khác muốn sử dụng.
     */
    public String getApiKey() {
        return this.googleMapsApiKey;
    }
}
