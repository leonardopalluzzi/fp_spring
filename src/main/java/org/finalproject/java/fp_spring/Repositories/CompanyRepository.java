package org.finalproject.java.fp_spring.Repositories;

import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Company;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompanyRepository
        extends PagingAndSortingRepository<Company, Integer>, JpaSpecificationExecutor<Company> {

    Optional<Company> findById(Integer id);

}
