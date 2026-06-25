package com.fixit.domain.upload.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.upload.cleanup")
public class UploadCleanupProperties {

    /**
     * Bật/tắt scheduler cleanup.
     */
    private boolean enabled = false;

    /**
     * Số bản ghi xử lý tối đa mỗi lần chạy.
     * Tránh trường hợp DB nhiều rác quá làm scheduler chạy nặng.
     */
    private int batchSize = 100;

    /**
     * Có xóa file trên Cloudinary khi upload PENDING bị quá hạn không.
     */
    private boolean deleteExpiredPendingFromStorage = true;

    /**
     * Có xử lý CONFIRMED nhưng chưa used_at quá lâu không.
     * Mặc định tắt để tránh xóa nhầm file hợp lệ trước khi làm đợt 5.
     */
    private boolean cleanupUnusedConfirmed = false;

    /**
     * Số giờ cho phép file CONFIRMED tồn tại mà chưa gắn nghiệp vụ.
     * Chỉ có tác dụng khi cleanupUnusedConfirmed = true.
     */
    private int unusedConfirmedExpireHours = 24;
}