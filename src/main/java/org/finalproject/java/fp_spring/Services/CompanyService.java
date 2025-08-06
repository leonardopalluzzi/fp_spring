package org.finalproject.java.fp_spring.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Services.Interfaces.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Company> GetAllFiltered(String email, String phone, LocalDateTime startDate, LocalDateTime endDate) {
        List<Company> companies = companyRepo
                .findAllByEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrCreatedAtBetween(email, phone,
                        startDate, endDate);

        return companies;
    }

}
