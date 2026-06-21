package com.fixit.global.util;

/**
 * Công cụ Toán học tính khoảng cách bề mặt quả địa cầu (Haversine Formula).
 * File này đóng vai trò là PHƯƠNG ÁN DỰ PHÒNG (Fallback) cho Google Maps.
 */
public class HaversineUtil {
    
    // Bán kính trung bình của Trái Đất (Km)
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Tính khoảng cách đường chim bay (Km) giữa 2 tọa độ (Vĩ độ, Kinh độ).
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double originLat = Math.toRadians(lat1);
        double destinationLat = Math.toRadians(lat2);

        // Công thức lượng giác phức tạp của Haversine
        double a = Math.pow(Math.sin(dLat / 2), 2) + 
                   Math.pow(Math.sin(dLon / 2), 2) * Math.cos(originLat) * Math.cos(destinationLat);
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return EARTH_RADIUS_KM * c;
    }

    /**
     * DỰ PHÒNG THỜI GIAN ĐI ĐƯỜNG
     * Nếu mạng đứt, Google không trả lời. Ta giả định tốc độ xe máy trong 
     * thành phố trung bình là 30km/h để ước lượng thời gian đi đến nhà khách.
     */
    public static double estimateDurationMins(double distanceKm) {
        double speedKmh = 30.0;
        return (distanceKm / speedKmh) * 60.0; // Đổi ra phút
    }
}
