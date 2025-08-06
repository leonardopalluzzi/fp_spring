package org.finalproject.java.fp_spring.Services;

import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Services.Interfaces.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminSerivce implements IAdminService {

    @Autowired
    CompanyRepository companyRepo;

    @Override
    public List<Company> GetAllPaginated() {
        List<Company> companies = companyRepo.findAll();

        return companies;
    }

}
