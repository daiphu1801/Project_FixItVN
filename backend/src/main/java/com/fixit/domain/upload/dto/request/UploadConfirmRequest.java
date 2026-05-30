package com.fixit.domain.upload.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UploadConfirmRequest {

    @NotNull(message = "uploadId không được để trống")
    private UUID uploadId;

    @NotBlank(message = "objectKey không được để trống")
    @Size(max = 500, message = "objectKey không được vượt quá 500 ký tự")
    private String objectKey;

    @NotBlank(message = "fileUrl không được để trống")
    @Size(max = 1000, message = "fileUrl không được vượt quá 1000 ký tự")
    private String fileUrl;

    @NotBlank(message = "Content-Type không được để trống")
    @Size(max = 100, message = "Content-Type không được vượt quá 100 ký tự")
    private String contentType;

    @NotNull(message = "Dung lượng file không được để trống")
    @Positive(message = "Dung lượng file phải lớn hơn 0")
    private Long fileSize;
}