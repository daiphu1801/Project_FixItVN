package com.fixit.feature.upload.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "pending_uploads",
        indices = {
                @Index("status"),
                @Index("purpose"),
                @Index("targetType"),
                @Index("groupId")
        }
)
public class PendingUploadEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String localFilePath;
    private String originalFileName;
    private String contentType;
    private long fileSize;
    private String purpose;

    private String uploadId;
    private String objectKey;
    private String uploadUrl;
    private String fileUrl;
    private String formDataJson;
    private long presignedExpiresAt;

    private String status;
    private String targetType;
    private String targetEntityId;
    private String groupId;
    private String slotKey;
    private String extraPayloadJson;

    private int retryCount;
    private String lastError;
    private long createdAt;
    private long updatedAt;
    private long lastAttemptAt;

    public PendingUploadEntity() {
    }

    public PendingUploadEntity(
            String localFilePath,
            String originalFileName,
            String contentType,
            long fileSize,
            String purpose,
            String status,
            String targetType,
            String targetEntityId,
            String groupId,
            String slotKey,
            String extraPayloadJson,
            long now
    ) {
        this.localFilePath = localFilePath;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.purpose = purpose;
        this.status = status;
        this.targetType = targetType;
        this.targetEntityId = targetEntityId;
        this.groupId = groupId;
        this.slotKey = slotKey;
        this.extraPayloadJson = extraPayloadJson;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFormDataJson() {
        return formDataJson;
    }

    public void setFormDataJson(String formDataJson) {
        this.formDataJson = formDataJson;
    }

    public long getPresignedExpiresAt() {
        return presignedExpiresAt;
    }

    public void setPresignedExpiresAt(long presignedExpiresAt) {
        this.presignedExpiresAt = presignedExpiresAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetEntityId() {
        return targetEntityId;
    }

    public void setTargetEntityId(String targetEntityId) {
        this.targetEntityId = targetEntityId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSlotKey() {
        return slotKey;
    }

    public void setSlotKey(String slotKey) {
        this.slotKey = slotKey;
    }

    public String getExtraPayloadJson() {
        return extraPayloadJson;
    }

    public void setExtraPayloadJson(String extraPayloadJson) {
        this.extraPayloadJson = extraPayloadJson;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getLastAttemptAt() {
        return lastAttemptAt;
    }

    public void setLastAttemptAt(long lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }
}
