package com.fixit.feature.worker.profile.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkerSkillsUpdateRequest {

    @SerializedName("skills")
    private final List<WorkerSkillUpsertItemRequest> skills;

    public WorkerSkillsUpdateRequest(List<WorkerSkillUpsertItemRequest> skills) {
        this.skills = skills;
    }
}