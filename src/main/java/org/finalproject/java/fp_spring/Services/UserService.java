package org.finalproject.java.fp_spring.Services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.finalproject.java.fp_spring.DTOs.UserAdminIndexDTO;
import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.DTOs.UserInputDTO;
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
import org.finalproject.java.fp_spring.Specifications.UserSpecifications;
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
    ServiceService serviceService;

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

    public List<UserDTO> getOperatorsByServiceDTO(Integer serviceId) throws NotFoundException {
        CompanyService service = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service Not Found"));
        List<User> employees = service.getOperators();
        List<User> admins = userRepo.findAllByCompanyId(service.getCompany().getId());
        List<UserDTO> employeesDTO = new ArrayList<>();

        for (User entity : employees) {
            employeesDTO.add(mapper.toUserDTO(entity));
        }
        for (User admin : admins) {
            employeesDTO.add(mapper.toUserDTO(admin));
        }

        return employeesDTO;
    }

    public List<User> getOperatorsByService(Integer serviceId) throws NotFoundException {
        CompanyService service = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service Not Found"));
        List<User> employees = service.getOperators();

        Role adminRole = roleRepo.findByName(RoleName.COMPANY_ADMIN);

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

    public Page<UserDTO> getAllForAdminFiltered(DatabaseUserDetails user, String username, String email,
            int page, String role, String list) {

        Integer companyId = user.getCompany().getId();
        Pageable pagination = PageRequest.of(page, 10);
        Specification<User> spec = Specification.<User>unrestricted()
                .and(usernameContains(username))
                .and(emailContains(email))
                .and(roleContains(role));

        Page<User> entities;

        if (list.equals("customers")) {

            entities = userRepo.findAll(spec.and(UserSpecifications.hasCustomerInCompany(companyId)), pagination);

        } else if (list.equals("operators")) {

            entities = userRepo.findAll(spec.and(UserSpecifications.hasEmployeeInCompany(companyId)), pagination);

        } else if (list.equals("admins")) {
            // popolo lista admin della company e converto in dto
            List<User> adminCompany = user.getCompany().getUsers();
            List<UserDTO> adminCompanyDTO = new ArrayList<>();
            for (User admin : adminCompany) {
                adminCompanyDTO.add(mapper.toUserDTO(admin));
            }

            return new PageImpl<>(adminCompanyDTO);
        } else {
            return new PageImpl<>(new ArrayList<>()); // se non c'è il flag restituisco lista vuota
        }

        List<UserDTO> listDTO = entities.getContent().stream().map(mapper::toUserDTO).toList();

        return new PageImpl<>(listDTO, pagination, entities.getTotalElements()); // costruttore apposito per page
                                                                                 // implementatio
    }

    public Page<UserDTO> getAllForEmployeeFiltered(DatabaseUserDetails user, String username, String email,
            int page, String role) {

        Integer operatorId = user.getId();

        Specification<User> sepc = Specification.<User>unrestricted()
                .and(usernameContains(username))
                .and(emailContains(email))
                .and(roleContains(role))
                .and(hasOperator(operatorId));
        Pageable pagination = PageRequest.of(page, 10);

        Page<User> customersEntity = userRepo.findAll(sepc, pagination);
        Page<UserDTO> customersDTO = new PageImpl<UserDTO>(new ArrayList<UserDTO>());
        List<User> customersListEntity = customersEntity.getContent();
        List<UserDTO> customersListDTO = new ArrayList<>();

        for (User customer : customersListEntity) {
            customersListDTO.add(mapper.toUserDTO(customer));
        }
        customersDTO = new PageImpl<UserDTO>(customersListDTO);

        return customersDTO;
    }

    public UserDTO getAllByIdRoleWise(DatabaseUserDetails user, Integer userId)
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

    public UserDTO save(DatabaseUserDetails currentUser, UserInputDTO newUser, Integer serviceId)
            throws AccessDeniedException, NotFoundException {

        // se company_admin fai senno no
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            // controllo se admin puo inserire user nel service
            CompanyService service = serviceRepo.findById(serviceId)
                    .orElseThrow(() -> new NotFoundException("Service Not Found"));

            boolean isRelated = currentUser.getCompany().getServices().stream()
                    .anyMatch(s -> s.getId().equals(service.getId()));

            if (isRelated) {
                // creo user a partire dal dto
                User newUserEntity = new User();
                Role employeeRole = roleRepo.findByName(RoleName.COMPANY_USER);
                Set<Role> rolesToSet = new HashSet<>();
                rolesToSet.add(employeeRole);
                newUserEntity.setUsername(newUser.getUsername());
                newUserEntity.setEmail(newUser.getEmail());
                newUserEntity.setRoles(rolesToSet);

                // settare service in user e viceversa per persistenza
                List<CompanyService> services = new ArrayList<>();
                services.add(service);
                newUserEntity.getServices().add(service);
                service.getOperators().add(newUserEntity);

                // cripto password
                String rawPassword = newUser.getPassword();
                newUserEntity.setPassword(passwordEncoder.encode(rawPassword));

                // salvare user
                User savedUser = userRepo.save(newUserEntity);
                return mapper.toUserDTO(savedUser);
            } else {
                throw new AccessDeniedException(
                        "You cannot insert an employee in a service that is not from your company");
            }

        } else {
            throw new AccessDeniedException("You don't have permission to excecute this action");
        }
    }

    public UserDTO update(DatabaseUserDetails currentUser, UserInputDTO userToUpdateDTO, Integer userId)
            throws AccessDeniedException, NotFoundException {

        // se company_admin fai senno no
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            boolean isRelated = currentUser.getCompany().getServices().stream()
                    .anyMatch(s -> s.getOperators().stream()
                            .anyMatch(o -> o.getId().equals(userId)));

            if (isRelated) {
                // creo user a partire dal dto
                User userToUpdateEntity = userRepo.findById(userId)
                        .orElseThrow(() -> new NotFoundException("User Not Found"));
                userToUpdateEntity.setUsername(userToUpdateDTO.getUsername());
                userToUpdateEntity.setEmail(userToUpdateDTO.getEmail());
                String rawPassword = userToUpdateDTO.getPassword();
                if (userToUpdateDTO.getPassword() != "") {
                    userToUpdateEntity.setPassword(passwordEncoder.encode(rawPassword));
                }

                User updatedUser = userRepo.save(userToUpdateEntity);

                return mapper.toUserDTO(updatedUser);
            } else {
                throw new AccessDeniedException(
                        "You cannot insert an employee in a service that is not from your company");
            }
        } else if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()))) {
            boolean isRelated = currentUser.getId().equals(userId);

            if (isRelated) {
                User userToUpdateEntity = userRepo.findById(userId)
                        .orElseThrow(() -> new NotFoundException("User Not Found"));
                userToUpdateEntity.setUsername(userToUpdateDTO.getUsername());
                userToUpdateEntity.setEmail(userToUpdateDTO.getEmail());
                String rawPassword = userToUpdateDTO.getPassword();
                userToUpdateEntity.setPassword(passwordEncoder.encode(rawPassword));

                User updatedUser = userRepo.save(userToUpdateEntity);

                return mapper.toUserDTO(updatedUser);
            } else {
                throw new AccessDeniedException("You don't have the authority to modify this resource");
            }
        } else {
            throw new AccessDeniedException("You don't have the authority to modify this resource");
        }
    }

    public void deleteByIdRoleWise(Integer userId, DatabaseUserDetails currentUser)
            throws UsernameNotFoundException, AccessDeniedException {
        User user = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            boolean isRelated = currentUser.getCompany().getServices().stream()
                    .anyMatch(s -> s.getOperators().stream().anyMatch(o -> o.getId().equals(userId)));

            if (isRelated) {
                user.getServices().stream().allMatch(s -> s.getOperators().remove(user));

                userRepo.deleteById(userId);

            } else {
                throw new AccessDeniedException("You don't have the authority to modify this resource");
            }

        } else if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()))) {
            boolean isRelated = currentUser.getId().equals(userId);

            if (isRelated) {

                user.getServices().stream().allMatch(s -> s.getCustomers().remove(user));
                userRepo.deleteById(userId);

            } else {
                throw new AccessDeniedException("You don't have the authority to modify this resource");

            }
        } else {
            throw new AccessDeniedException("You don't have the authority to modify this resource");
        }

    }

}
