package com.fixit.domain.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VnptKycConfigResponse {
    private String tokenId;
    private String tokenKey;
    private String apiUrl;
}
