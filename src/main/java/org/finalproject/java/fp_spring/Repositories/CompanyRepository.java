package org.finalproject.java.fp_spring.Repositories;

import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository
        extends JpaRepository<Company, Integer>,
        JpaSpecificationExecutor<Company> {

    Optional<Company> findById(Integer id);

}
