package org.finalproject.java.fp_spring.Repositories;

import org.finalproject.java.fp_spring.Models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

}
