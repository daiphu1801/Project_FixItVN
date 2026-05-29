package com.fixit.feature.worker.profile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkerSkillsResponse {

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("totalItems")
    private Integer totalItems;

    @SerializedName("skills")
    private List<WorkerSkillResponse> skills;

    public String getWorkerId() {
        return workerId;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public List<WorkerSkillResponse> getSkills() {
        return skills;
    }
}