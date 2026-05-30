package com.fixit.domain.upload.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresignedUrlRequest {

    @NotBlank(message = "Mục đích upload không được để trống")
    private String purpose;

    @Size(max = 255, message = "Tên file gốc không được vượt quá 255 ký tự")
    private String originalFileName;

    @NotBlank(message = "Content-Type không được để trống")
    @Size(max = 100, message = "Content-Type không được vượt quá 100 ký tự")
    private String contentType;

    @NotNull(message = "Dung lượng file không được để trống")
    @Positive(message = "Dung lượng file phải lớn hơn 0")
    private Long fileSize;
}