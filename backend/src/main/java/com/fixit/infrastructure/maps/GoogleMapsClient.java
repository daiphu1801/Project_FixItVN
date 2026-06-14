package com.fixit.infrastructure.maps;

import com.fixit.global.config.GoogleMapsConfig;
import com.fixit.infrastructure.maps.dto.GoogleMapsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client chịu trách nhiệm giao tiếp trực tiếp với Google Maps API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMapsClient {

    private final RestTemplate googleMapsRestTemplate;
    private final GoogleMapsConfig googleMapsConfig;

    // Đường dẫn gốc của API Distance Matrix
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    /**
     * Lấy khoảng cách và thời gian đi đường giữa 2 điểm.
     * 
     * @param originLat Tọa độ vĩ độ điểm xuất phát (Thợ)
     * @param originLng Tọa độ kinh độ điểm xuất phát (Thợ)
     * @param destLat Tọa độ vĩ độ điểm đến (Khách)
     * @param destLng Tọa độ kinh độ điểm đến (Khách)
     * @return GoogleMapsResponse chứa duration (thời gian) và distance (khoảng cách)
     */
    public GoogleMapsResponse getDistanceMatrix(double originLat, double originLng, double destLat, double destLng) {
        
        // Cấu trúc URL: https://maps.googleapis.com/.../json?origins=lat,lng&destinations=lat,lng&key=YOUR_API_KEY
        String url = UriComponentsBuilder.fromHttpUrl(DISTANCE_MATRIX_URL)
                .queryParam("origins", originLat + "," + originLng)
                .queryParam("destinations", destLat + "," + destLng)
                .queryParam("key", googleMapsConfig.getApiKey())
                .toUriString();

        log.debug("Calling Google Maps API for distance matrix. Origin: [{}, {}], Dest: [{}, {}]", 
                  originLat, originLng, destLat, destLng);

        try {
            // Thực hiện gửi HTTP GET request lên Google
            GoogleMapsResponse response = googleMapsRestTemplate.getForObject(url, GoogleMapsResponse.class);
            
            if (response != null && "OK".equals(response.getStatus())) {
                return response;
            } else {
                log.warn("Google Maps API returned non-OK status: {}", 
                         response != null ? response.getStatus() : "NULL RESPONSE");
                return null;
            }
        } catch (Exception e) {
            log.error("Error while calling Google Maps API", e);
            // Trả về null nếu bị lỗi mạng/timeout để thuật toán không bị crash
            return null;
        }
    }
}
