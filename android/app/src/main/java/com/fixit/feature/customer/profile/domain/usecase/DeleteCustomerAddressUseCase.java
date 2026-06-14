package com.fixit.feature.customer.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import javax.inject.Inject;

public class DeleteCustomerAddressUseCase {
    private final CustomerProfileRepository repository;

    @Inject
    public DeleteCustomerAddressUseCase(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(String addressId, ResultCallback<Void> callback) {
        repository.deleteAddress(addressId, callback);
    }
}
