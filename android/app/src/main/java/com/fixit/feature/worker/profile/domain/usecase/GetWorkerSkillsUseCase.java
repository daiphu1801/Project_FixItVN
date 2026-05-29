package com.fixit.feature.worker.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;
import com.fixit.feature.worker.profile.domain.repository.WorkerProfileRepository;

import java.util.List;

import javax.inject.Inject;

public class GetWorkerSkillsUseCase {

    private final WorkerProfileRepository repository;

    @Inject
    public GetWorkerSkillsUseCase(WorkerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<WorkerSkill>> callback) {
        repository.getSkills(callback);
    }
}