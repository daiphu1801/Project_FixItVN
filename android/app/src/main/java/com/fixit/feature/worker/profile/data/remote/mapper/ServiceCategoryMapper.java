package com.fixit.feature.worker.profile.data.remote.mapper;

import com.fixit.feature.worker.profile.data.remote.dto.response.ServiceCategoryResponse;
import com.fixit.feature.worker.profile.domain.model.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

public class ServiceCategoryMapper {

    private ServiceCategoryMapper() {
    }

    public static ServiceCategory toDomain(ServiceCategoryResponse response) {
        if (response == null) {
            return null;
        }

        return new ServiceCategory(
                response.getId() == null ? 0 : response.getId(),
                response.getServiceName() == null ? "" : response.getServiceName()
        );
    }

    public static List<ServiceCategory> toDomainList(List<ServiceCategoryResponse> responses) {
        List<ServiceCategory> result = new ArrayList<>();

        if (responses == null) {
            return result;
        }

        for (ServiceCategoryResponse response : responses) {
            ServiceCategory domain = toDomain(response);
            if (domain != null) {
                result.add(domain);
            }
        }

        return result;
    }
}
