package com.fixit.domain.worker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerProfileUpdateRequest {

    @Size(max = 20, message = "Họ tên không được vượt quá 20 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    private String avatarUrl;

    private String experienceDescription;

    @Size(max = 255, message = "Khu vực hoạt động không được vượt quá 255 ký tự")
    private String serviceArea;
}