package com.fixit.domain.wallet.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawApproveRequest {
    private String referenceCode;
    private String adminNote;
}
