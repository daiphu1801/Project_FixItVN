package com.fixit.infrastructure.maps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * DTO hứng dữ liệu trả về từ Google Distance Matrix API.
 * Google trả về cấu trúc rất sâu (nhiều mảng lồng nhau), 
 * nên class này phải cấu trúc y hệt để Spring tự động parse (bóc tách) JSON.
 */
@Data
public class GoogleMapsResponse {
    
    // Status chung của toàn bộ request (VD: "OK", "INVALID_REQUEST")
    private String status;
    
    // Danh sách các hàng kết quả (Mỗi hàng ứng với 1 điểm xuất phát)
    private List<Row> rows;

    @Data
    public static class Row {
        // Danh sách các cột (Mỗi cột ứng với 1 điểm đến)
        private List<Element> elements;
    }

    @Data
    public static class Element {
        // Status của riêng cặp điểm này (VD: "OK", "ZERO_RESULTS")
        private String status;
        
        // Thời gian đi xe thực tế (Có xét kẹt xe nếu có thông tin)
        private Duration duration;
        
        // Khoảng cách theo đường xe chạy
        private Distance distance;
    }

    @Data
    public static class Duration {
        @JsonProperty("text")
        private String text; // Ví dụ: "15 mins" (Dùng để hiển thị)
        
        @JsonProperty("value")
        private long value;  // Ví dụ: 900 (Giây) - Dùng để tính toán thuật toán
    }

    @Data
    public static class Distance {
        @JsonProperty("text")
        private String text; // Ví dụ: "5.5 km"
        
        @JsonProperty("value")
        private long value;  // Ví dụ: 5500 (Mét) - Dùng để tính toán
    }
}
