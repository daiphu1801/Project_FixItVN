package com.fixit.feature.customer.complaint.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ComplaintRequestDto {
    @SerializedName("customerReason")
    private String customerReason;

    @SerializedName("evidenceImageUrls")
    private List<String> evidenceImageUrls;

    public ComplaintRequestDto() {}

    public ComplaintRequestDto(String customerReason, List<String> evidenceImageUrls) {
        this.customerReason = customerReason;
        this.evidenceImageUrls = evidenceImageUrls;
    }

    public String getCustomerReason() { return customerReason; }
    public void setCustomerReason(String customerReason) { this.customerReason = customerReason; }

    public List<String> getEvidenceImageUrls() { return evidenceImageUrls; }
    public void setEvidenceImageUrls(List<String> evidenceImageUrls) { this.evidenceImageUrls = evidenceImageUrls; }
}
