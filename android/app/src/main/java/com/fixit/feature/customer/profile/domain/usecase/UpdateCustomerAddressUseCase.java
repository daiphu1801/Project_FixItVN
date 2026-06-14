package com.fixit.feature.customer.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import javax.inject.Inject;

public class UpdateCustomerAddressUseCase {
    private final CustomerProfileRepository repository;

    @Inject
    public UpdateCustomerAddressUseCase(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(String addressId, CustomerAddress address, ResultCallback<CustomerAddress> callback) {
        repository.updateAddress(addressId, address, callback);
    }
}
