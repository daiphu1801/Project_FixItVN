package com.fixit.feature.customer.complaint.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkerComplaintResponseRequestDto {
    @SerializedName("workerResponse")
    private String workerResponse;

    @SerializedName("evidenceImageUrls")
    private List<String> evidenceImageUrls;

    public WorkerComplaintResponseRequestDto() {}

    public WorkerComplaintResponseRequestDto(String workerResponse, List<String> evidenceImageUrls) {
        this.workerResponse = workerResponse;
        this.evidenceImageUrls = evidenceImageUrls;
    }

    public String getWorkerResponse() { return workerResponse; }
    public void setWorkerResponse(String workerResponse) { this.workerResponse = workerResponse; }

    public List<String> getEvidenceImageUrls() { return evidenceImageUrls; }
    public void setEvidenceImageUrls(List<String> evidenceImageUrls) { this.evidenceImageUrls = evidenceImageUrls; }
}
