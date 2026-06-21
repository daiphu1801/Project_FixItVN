package com.fixit.domain.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VnptFaceMatchResult {
    private boolean success;
    private double similarityScore; // 0 to 100
    private String errorCode;
    private String errorMessage;
}
