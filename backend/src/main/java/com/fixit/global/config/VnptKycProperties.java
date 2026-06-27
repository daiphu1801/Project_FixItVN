package com.fixit.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.vnpt.ekyc")
public class VnptKycProperties {
    private String apiUrl = "https://api-ekyc.vnpt.vn/api/v1";
    private String clientId;
    private String clientSecret;
    private String tokenId;
    private String tokenKey;
    private double similarityThreshold = 80.0;
}
