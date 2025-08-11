package org.finalproject.java.fp_spring.Services.Interfaces;

import java.time.LocalDateTime;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Company;
import org.springframework.data.domain.Page;

public interface ICompanyService {
    Page<Company> GetAllFiltered(String name, String email, String phone, LocalDateTime startDate,
            LocalDateTime endDate, Integer page);

    Optional<Company> show(Integer id);

    void store(Company company);

    void deleteById(Integer id);
}
