package com.fixit.feature.customer.booking.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class CustomerBookingCancelRequestDto {
    @SerializedName("reason")
    private String reason;

    @SerializedName("isWorkerFault")
    private boolean isWorkerFault;

    public CustomerBookingCancelRequestDto(String reason, boolean isWorkerFault) {
        this.reason = reason;
        this.isWorkerFault = isWorkerFault;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isWorkerFault() { return isWorkerFault; }
    public void setWorkerFault(boolean isWorkerFault) { this.isWorkerFault = isWorkerFault; }
}
