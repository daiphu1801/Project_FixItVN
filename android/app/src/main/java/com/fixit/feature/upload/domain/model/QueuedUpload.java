package com.fixit.feature.upload.domain.model;

public class QueuedUpload {
    private final long id;
    private final String purpose;
    private final String status;

    public QueuedUpload(long id, String purpose, String status) {
        this.id = id;
        this.purpose = purpose;
        this.status = status;
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
}
