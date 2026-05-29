package com.fixit.domain.service_categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fixit.domain.service_categories.entity.ServiceCategory;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Integer> // kế thừa interface
                                                                                           // JpaRepository để có các
                                                                                           // phương thức cơ bản như
                                                                                           // save(), findById(),
                                                                                           // findAll(), delete(),...
{
}
