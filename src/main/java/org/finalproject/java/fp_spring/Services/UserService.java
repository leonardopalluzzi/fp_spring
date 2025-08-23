package org.finalproject.java.fp_spring.Services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.finalproject.java.fp_spring.DTOs.UserAdminIndexDTO;
import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    public UserAdminIndexDTO getAllForAdminFiltered(DatabaseUserDetails user, String username, String email,
            int page) {

        Integer companyId = user.getCompany().getId();
        Pageable pagination = PageRequest.of(page, 10);
        Specification<User> spec = Specification.<User>unrestricted()
                .and(usernameContains(username))
                .and(emailContains(email));

        UserAdminIndexDTO usersLists = new UserAdminIndexDTO();

        // popolo lista admin della company e converto in dto
        List<User> adminCompany = user.getCompany().getUsers();
        List<UserDTO> adminCompanyDTO = new ArrayList<>();
        for (User admin : adminCompany) {
            adminCompanyDTO.add(mapper.toUserDTO(admin));
        }

        // popolo lista customer della company e converto in dto
        Page<User> custmomersEntity = userRepo.findCustomersByCompanyId(companyId, spec, pagination);
        Page<UserDTO> customers = new PageImpl<UserDTO>(new ArrayList<UserDTO>());
        List<User> usersContent = custmomersEntity.getContent();
        List<UserDTO> usersDTO = new ArrayList<>();

        for (User userEntity : usersContent) {
            usersDTO.add(mapper.toUserDTO(userEntity));
        }
        customers = new PageImpl<UserDTO>(usersDTO);

        // popolo lista impiegati della company e converto in dto
        Page<User> employeesEntity = userRepo.findEmployeesByCompanyId(companyId, spec, pagination);
        Page<UserDTO> employeesDTO = new PageImpl<UserDTO>(new ArrayList<UserDTO>());
        List<User> employeesEntityList = employeesEntity.getContent();
        List<UserDTO> employeesDTOList = new ArrayList<>();

        for (User employeeEntity : employeesEntityList) {
            employeesDTOList.add(mapper.toUserDTO(employeeEntity));
        }
        employeesDTO = new PageImpl<UserDTO>(employeesDTOList);

        // assegno i risultati nel dto
        usersLists.setCompanyAdmins(adminCompanyDTO);
        usersLists.setCustomers(customers);
        usersLists.setEmployees(employeesDTO);

        return usersLists;
    }

    public Page<UserDTO> getAllForEmployeeFiltered(DatabaseUserDetails user, String username, String email,
            int page) {

        Specification<User> sepc = Specification.<User>unrestricted()
                .and(usernameContains(username))
                .and(emailContains(email));
        Pageable pagination = PageRequest.of(page, 10);
        Integer operatorId = user.getId();

        Page<User> customersEntity = userRepo.findCustomerByOperatorId(operatorId, sepc, pagination);
        Page<UserDTO> customersDTO = new PageImpl<UserDTO>(new ArrayList<UserDTO>());
        List<User> customersListEntity = customersEntity.getContent();
        List<UserDTO> customersListDTO = new ArrayList<>();

        for (User customer : customersListEntity) {
            customersListDTO.add(mapper.toUserDTO(customer));
        }
        customersDTO = new PageImpl<UserDTO>(customersListDTO);

        return customersDTO;
    }

    public UserDTO getByIdRoleWise(DatabaseUserDetails user, Integer userId)
            throws AccessDeniedException, NotFoundException {
        boolean isAdmin = user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()));
        boolean isEmployee = user.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()));
        boolean isCustomer = user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()));
        User userToSend = new User();

        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Requeseted User Not Found");
        }

        // la chain andrebbe girata al contrario ma per testing la tengo cosi
        if (isAdmin) {
            // confronto i valori degli id perche il contains usa l'equals che confornta le
            // istanze non avendo un override in user, quindi confrontado id sono sempre
            // sicuro di trovare corrispondenza
            boolean isRelated = user.getCompany().getUsers().stream()
                    .anyMatch(u -> u.getId().equals(userId))
                    || user.getCompany().getServices().stream().anyMatch(
                            s -> s.getCustomers().stream().anyMatch(c -> c.getId().equals(userId)))
                    || user.getCompany().getServices().stream().anyMatch(
                            s -> s.getOperators().stream().anyMatch(o -> o.getId().equals(userId)))
                    || user.getId().equals(userId);

            if (isRelated) {
                userToSend = userRepo.findById(userId).get();
            } else {
                throw new AccessDeniedException("You don't have permission to access this resource");
            }
        } else if (isEmployee) {
            boolean isRelated = user.getCompany().getServices().stream()
                    .anyMatch(s -> s.getCustomers().stream().anyMatch(c -> c.getId().equals(userId)));

            if (isRelated) {
                userToSend = userRepo.findById(userId).get();
            } else {
                throw new AccessDeniedException("You don't have permission to access this resource");
            }
        } else if (isCustomer) {
            if (user.getId().equals(userId)) {
                userToSend = userRepo.findById(userId).get();
            } else {
                throw new AccessDeniedException("You don't have permission to access this resource");
            }
        } else {
            throw new AccessDeniedException("User not logged or role not recognized");
        }

        return mapper.toUserDTO(userToSend);
    }

}
