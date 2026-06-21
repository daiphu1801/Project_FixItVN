package com.fixit.feature.customer.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import java.util.List;

import javax.inject.Inject;

// Nút bấm nghiệp vụ: Lấy danh sách Địa chỉ
public class GetCustomerAddressesUseCase {

    private final CustomerProfileRepository repository;

    @Inject
    public GetCustomerAddressesUseCase(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    // Hàm gọi tới Bộ não (Repository)
    public void execute(ResultCallback<List<CustomerAddress>> callback) {
        repository.getAddresses(callback);
    }
}
