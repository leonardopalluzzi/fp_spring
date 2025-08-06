package org.finalproject.java.fp_spring.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository extends JpaRepository<Company, Integer>, JpaSpecificationExecutor<Company> {

    List<Company> findAllByNameContaining(String name);

    List<Company> findAllByEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrCreatedAtBetween(String email,
            String phone, LocalDateTime startDate, LocalDateTime endDate);

}
