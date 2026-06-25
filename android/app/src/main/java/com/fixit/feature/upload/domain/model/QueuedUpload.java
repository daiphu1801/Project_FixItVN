package com.fixit.feature.upload.domain.model;

public class QueuedUpload {
    private final long id;
    private final String purpose;
    private final String status;
    private final String slotKey;
    private final String lastError;
    private final int retryCount;
    private final String groupId;

    public QueuedUpload(long id, String purpose, String status) {
        this(id, purpose, status, null, null, 0, null);
    }

    public QueuedUpload(long id, String purpose, String status, String slotKey, String lastError, int retryCount, String groupId) {
        this.id = id;
        this.purpose = purpose;
        this.status = status;
        this.slotKey = slotKey;
        this.lastError = lastError;
        this.retryCount = retryCount;
        this.groupId = groupId;
    }

    public long getId() {
        return id;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }

    public String getSlotKey() {
        return slotKey;
    }

    public String getLastError() {
        return lastError;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getGroupId() {
        return groupId;
    }
}

