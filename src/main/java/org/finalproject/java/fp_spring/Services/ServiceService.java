package org.finalproject.java.fp_spring.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.management.relation.RoleInfoNotFoundException;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.Interfaces.IServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceService implements IServiceService {

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Override
    public String generateServiceCode() {
        return "SVC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public CompanyService createService(CompanyService service) {

        String code;

        // check if code already exists
        do {
            code = generateServiceCode();
        } while (serviceRepo.existsByCode(code));

        service.setCode(code);

        return serviceRepo.save(service);
    }

    @Transactional
    public List<CompanyService> findAllByUser(DatabaseUserDetails user) throws RoleInfoNotFoundException {
        Set<GrantedAuthority> roles = user.getAuthorities();
        Role emplyeeRole = roleRepo.findByName(RoleName.COMPANY_USER);
        Role customerRole = roleRepo.findByName(RoleName.CLIENT);

        GrantedAuthority employeeAuthority = new SimpleGrantedAuthority(emplyeeRole.toString());
        GrantedAuthority customerAuthority = new SimpleGrantedAuthority(customerRole.toString());

        List<CompanyService> services = new ArrayList<>();

        if (roles.isEmpty()) {
            throw new RoleInfoNotFoundException("Role not found");
        }

        if (roles.contains(employeeAuthority) && !roles.contains(customerAuthority)) {
            services = serviceRepo.findByOperators_id(user.getId());
        } else if (roles.contains(customerAuthority) && !roles.contains(employeeAuthority)) {
            services = serviceRepo.findByCustomers_id(user.getId());
        }

        return services;
    }
}
