package com.fixit.feature.worker.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;
import com.fixit.feature.worker.profile.domain.repository.WorkerProfileRepository;

import java.util.List;

import javax.inject.Inject;

public class UpdateWorkerSkillsUseCase {

    private final WorkerProfileRepository repository;

    @Inject
    public UpdateWorkerSkillsUseCase(WorkerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(
            List<WorkerSkill> skills,
            ResultCallback<List<WorkerSkill>> callback
    ) {
        repository.updateSkills(skills, callback);
    }
}