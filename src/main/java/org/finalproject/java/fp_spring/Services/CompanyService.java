package org.finalproject.java.fp_spring.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.management.ServiceNotFoundException;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.Attachment;
import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.AttachmentRepository;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
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

import jakarta.persistence.EntityManager;

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

    @Autowired
    ServiceRepository serviceRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    TicketsRepository ticketRepo;

    @Autowired
    AttachmentRepository attachmentRepo;

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

        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found"));

        // tt
        List<Ticket> ttToDelete = company.getServices().stream().flatMap(s -> s.getTickets().stream()).toList();

        for (Ticket ticket : ttToDelete) {
            User requester = ticket.getRequester();
            requester.getUserTickets().remove(ticket);
            ticket.setAssignedTo(null);
            ticket.setType(null);
            for (Attachment a : ticket.getAttachments()) {
                attachmentRepo.delete(a);
            }
            // tt history
        }

        // service
        List<org.finalproject.java.fp_spring.Models.CompanyService> servicesToDelete = company.getServices();

        for (org.finalproject.java.fp_spring.Models.CompanyService s : servicesToDelete) {
            s.getOperators().clear();
            s.getCustomers().clear();
            s.getTickets().clear();
            serviceRepo.save(s);
        }

        ticketRepo.deleteAll(ttToDelete);
        serviceRepo.deleteAll(servicesToDelete);
        companyRepo.delete(company);

    }

    // utils
    public void deleteService(Integer id) throws ServiceNotFoundException, Exception {
        org.finalproject.java.fp_spring.Models.CompanyService serviceToDelete = serviceRepo.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));

        serviceRepo.deleteCustom(serviceToDelete.getId());

        if (serviceRepo.existsById(serviceToDelete.getId())) {
            throw new Exception("Service Not Deleted");
        }
    }

}
