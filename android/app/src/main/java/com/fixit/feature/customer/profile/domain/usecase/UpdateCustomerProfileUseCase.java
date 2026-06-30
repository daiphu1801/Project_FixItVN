package com.fixit.feature.customer.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import javax.inject.Inject;

public class UpdateCustomerProfileUseCase {

    private final CustomerProfileRepository repository;

    @Inject
    public UpdateCustomerProfileUseCase(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(String fullName, String email, String gender, String dob, ResultCallback<CustomerProfile> callback) {
        repository.updateProfile(fullName, email, gender, dob, callback);
    }
}
