package com.fixit.domain.service_categories.repository;

import com.fixit.domain.service_categories.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Integer> {
}