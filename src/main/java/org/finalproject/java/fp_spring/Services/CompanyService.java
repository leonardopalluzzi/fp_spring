package org.finalproject.java.fp_spring.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.finalproject.java.fp_spring.Services.Interfaces.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.finalproject.java.fp_spring.Specifications.CompanySpecifications.*;

@Service
public class CompanyService implements ICompanyService {

    @Autowired
    CompanyRepository companyRepo;

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
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

    @Override
    public Optional<Company> show(Integer id) {
        Optional<Company> company = companyRepo.findById(id);
        return company;
    }

    @Transactional
    @Override
    public void store(Company company) {
        if (company.getUsers() == null || company.getUsers().isEmpty()) {
            throw new RuntimeException("You must insert at least one company admin");
        }

        // recupero il ruolo
        Set<Role> roles = new HashSet<>();
        Role role = roleRepo.findByName(RoleName.COMPANY_ADMIN);
        roles.add(role);

        // setto il ruolo per gli user
        for (User user : company.getUsers()) {
            user.setRoles(roles);
            user.setCompany(company);

            String rawPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        // salvo company in db
        companyRepo.save(company);

    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Optional<Company> companyOpt = companyRepo.findById(id);

        if (companyOpt.isEmpty()) {
            return;
        }

        Company company = companyOpt.get();
        System.out.println("eliminando la comapny: " + company.getId());

        for (User user : company.getUsers()) {
            user.getRoles().clear();
        }

        companyRepo.delete(company);
    }

}
