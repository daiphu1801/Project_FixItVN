package com.fixit.feature.customer.workerprofile.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerProfile;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerSkill;

import java.util.List;

public interface PublicWorkerRepository {
    void getWorkerProfile(String workerId, ResultCallback<PublicWorkerProfile> callback);
    void getWorkerSkills(String workerId, ResultCallback<List<PublicWorkerSkill>> callback);
}
