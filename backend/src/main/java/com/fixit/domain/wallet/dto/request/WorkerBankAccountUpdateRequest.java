package com.fixit.domain.wallet.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkerBankAccountUpdateRequest {

    @Size(max = 100, message = "Tên ngân hàng không được vượt quá 100 ký tự")
    private String bankName;

    @Size(max = 50, message = "Số tài khoản không được vượt quá 50 ký tự")
    @Pattern(regexp = "^$|^[0-9]{4,50}$", message = "Số tài khoản chỉ được chứa chữ số và có ít nhất 4 số")
    private String accountNumber;

    @Size(max = 100, message = "Tên chủ tài khoản không được vượt quá 100 ký tự")
    private String accountName;
}