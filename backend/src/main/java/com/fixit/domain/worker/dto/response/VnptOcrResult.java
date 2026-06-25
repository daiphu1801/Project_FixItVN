package com.fixit.domain.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VnptOcrResult {
    private boolean success;
    private String idNumber;
    private String fullName;
    private String errorCode;
    private String errorMessage;
}
