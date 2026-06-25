package com.fixit.feature.customer.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import javax.inject.Inject;

public class AddCustomerAddressUseCase {
    private final CustomerProfileRepository repository;

    @Inject
    public AddCustomerAddressUseCase(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(CustomerAddress address, ResultCallback<CustomerAddress> callback) {
        repository.addAddress(address, callback);
    }
}
