package com.fixit.feature.worker.profile.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;

import java.util.List;

public interface WorkerProfileRepository {

    void getProfile(ResultCallback<WorkerProfile> callback);

    void updateProfile(
            WorkerProfileUpdateInput input,
            ResultCallback<WorkerProfile> callback
    );

    void getSkills(ResultCallback<List<WorkerSkill>> callback);

    void updateSkills(
            List<WorkerSkill> skills,
            ResultCallback<List<WorkerSkill>> callback
    );
}