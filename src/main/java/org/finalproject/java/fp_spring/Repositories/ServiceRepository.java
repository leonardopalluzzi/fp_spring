package org.finalproject.java.fp_spring.Repositories;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<CompanyService, Integer> {

    boolean existsByCode(String code);
}