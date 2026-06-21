package com.fixit.feature.customer.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import javax.inject.Inject;

public class SetDefaultCustomerAddressUseCase {
    private final CustomerProfileRepository repository;

    @Inject
    public SetDefaultCustomerAddressUseCase(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(String addressId, ResultCallback<CustomerAddress> callback) {
        repository.setDefaultAddress(addressId, callback);
    }
}
