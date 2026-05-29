package com.fixit.feature.worker.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;
import com.fixit.feature.worker.profile.domain.repository.WorkerProfileRepository;

import javax.inject.Inject;

public class UpdateWorkerProfileUseCase {

    private final WorkerProfileRepository repository;

    @Inject
    public UpdateWorkerProfileUseCase(WorkerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(
            WorkerProfileUpdateInput input,
            ResultCallback<WorkerProfile> callback
    ) {
        repository.updateProfile(input, callback);
    }
}