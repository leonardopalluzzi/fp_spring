package org.finalproject.java.fp_spring.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Services.Interfaces.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import static org.finalproject.java.fp_spring.Specifications.CompanySpecifications.*;

@Service
public class CompanyService implements ICompanyService {

    @Autowired
    CompanyRepository companyRepo;

    @Override
    public List<Company> GetAllPaginated() {

        List<Company> companies = companyRepo.findAll();

        return companies;
    }

    public List<Company> GetAllPaginatedByName(String name) {
        List<Company> companies = companyRepo.findAllByNameContaining(name);

        return companies;
    }

    public Page<Company> GetAllFiltered(String name, String email, String phone, LocalDateTime startDate,
            LocalDateTime endDate, Integer page) {
        Specification<Company> spec = Specification.<Company>unrestricted().and(nameContains(name))
                .and(emailContains(email))
                .and(phoneContains(phone))
                .and(createdAtBetween(startDate, endDate));

        Pageable pagination = PageRequest.of(page, 10);

        Page<Company> companies = companyRepo.findAll(spec, pagination);
        return companies;
    }

}
