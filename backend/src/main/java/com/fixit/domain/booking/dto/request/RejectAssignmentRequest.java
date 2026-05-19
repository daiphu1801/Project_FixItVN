package com.fixit.domain.booking.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectAssignmentRequest {

    /**
     * Ví dụ:
     * - TOO_FAR
     * - BUSY
     * - OUT_OF_SKILL
     * - OTHER
     */
    @Size(max = 50, message = "Nhóm lý do từ chối không được vượt quá 50 ký tự")
    private String reasonCategory;

    /**
     * Lý do chi tiết do thợ nhập.
     */
    @Size(max = 500, message = "Lý do từ chối không được vượt quá 500 ký tự")
    private String reason;
}