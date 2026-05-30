package com.fixit.domain.service_categories.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fixit.domain.service_categories.entity.ServiceItem;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Integer> {
    List<ServiceItem> findByServiceCategoryId(Integer categoryId);
}
