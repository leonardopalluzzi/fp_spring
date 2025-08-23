package org.finalproject.java.fp_spring.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.Interfaces.IUserService;
import org.finalproject.java.fp_spring.ViewModels.UsersVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.finalproject.java.fp_spring.Specifications.UserSpecifications.*;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    CompanyRepository companyRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    ServiceRepository serviceRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MapperService mapper;

    @Override
    public void deleteById(Integer userId) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findById(userId);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found");
        }

        userRepo.deleteById(userId);
    }

    public List<User> findByCompany(Integer companyId) {
        Optional<Company> company = companyRepo.findById(companyId);
        List<User> users = userRepo.findByCompany(company.get());
        return users;
    }

    public User getById(Integer id) {
        Optional<User> user = userRepo.findById(id);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public Optional<User> findById(Integer userId) {
        return userRepo.findById(userId);
    }

    public void save(User user) {
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepo.save(user);
    }

    public User insertNewUser(User user, Integer companyId) {
        User userWithCompany = setCompanyById(user, companyId);
        User updatedUser = setAdminRole(userWithCompany);
        String rawPassword = updatedUser.getPassword();
        updatedUser.setPassword(passwordEncoder.encode(rawPassword));

        userRepo.save(updatedUser);

        return updatedUser;
    }

    public User setCompanyById(User user, Integer companyId) {
        Optional<Company> company = companyRepo.findById(companyId);
        user.setCompany(company.get());

        return user;
    }

    public User setAdminRole(User user) {
        Role role = roleRepo.findByName(RoleName.COMPANY_ADMIN);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);

        return user;
    }

    public List<User> getOperatorsByService(Integer serviceId) {
        CompanyService service = serviceRepo.findById(serviceId).get();
        List<User> employees = service.getOperators();

        return employees;
    }

    public List<User> getCustomersByService(Integer serviceId) {
        CompanyService service = serviceRepo.findById(serviceId).get();
        List<User> customers = service.getCustomers();

        return customers;
    }

    public UsersVM findByService(Integer serviceId) {
        List<User> allUsers = userRepo.findByServicesId(serviceId);
        Role employeeRole = new Role(RoleName.COMPANY_USER);
        Role customerRole = new Role(RoleName.CLIENT);

        List<User> employees = new ArrayList<>();
        List<User> customers = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getRoles().contains(employeeRole)) {
                employees.add(user);
            } else if (user.getRoles().contains(customerRole)) {
                customers.add(user);
            }
        }

        UsersVM users = new UsersVM(employees, customers);

        return users;
    }

    public Integer findCompanyIdByServiceId(Integer serviceId) {
        CompanyService service = serviceRepo.findById(serviceId).get();
        Integer companyId = service.getCompany().getId();

        return companyId;
    }

    public Page<UserDTO> getAllFiltered(DatabaseUserDetails user, String name, String email, int page) {

        Specification<User> spec = Specification.<User>unrestricted()
                .and(usernameContains(name))
                .and(emailContains(email));

        Pageable pagination = PageRequest.of(page, 10);

        Page<User> users = userRepo.findAll(spec, pagination);
        Page<UserDTO> usersDTO = users.map(u -> mapper.toUserDTO(u));

        return usersDTO;
    }
}
