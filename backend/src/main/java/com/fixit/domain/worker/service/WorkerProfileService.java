package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.request.WorkerProfileUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerSkillsUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerProfileResponse;
import com.fixit.domain.worker.dto.response.WorkerSkillsResponse;

public interface WorkerProfileService {

    WorkerProfileResponse getMyProfile();

    WorkerProfileResponse updateMyProfile(WorkerProfileUpdateRequest request);

    WorkerSkillsResponse getMySkills();

    WorkerSkillsResponse updateMySkills(WorkerSkillsUpdateRequest request);
}