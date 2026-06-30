package com.fixit.feature.worker.home.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class WorkerStatusUpdateRequest {
    @SerializedName("available")
    private final boolean available;

    public WorkerStatusUpdateRequest(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
}
