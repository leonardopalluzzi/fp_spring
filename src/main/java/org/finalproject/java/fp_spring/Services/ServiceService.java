package org.finalproject.java.fp_spring.Services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.management.ServiceNotFoundException;
import javax.management.relation.RoleInfoNotFoundException;

import org.finalproject.java.fp_spring.DTOs.CompanyServiceDTO;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceInputDTO;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceUpdateDTO;
import org.finalproject.java.fp_spring.DTOs.TicketTypeDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Enum.ServiceStatus;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.Interfaces.IServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.finalproject.java.fp_spring.Specifications.ServiceSpecifications.*;

@Service
public class ServiceService implements IServiceService {

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private MapperService mapper;

    @Autowired
    ServiceTypeRepository serviceTypeRepo;

    @Autowired
    TicketTypeRepository ticketTypeRepo;

    @Autowired
    CompanyRepository companyRepo;

    @Autowired
    TicketsRepository ticketRepo;

    @PersistenceContext
    private EntityManager em;

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
    public Page<CompanyService> findAllByUser(DatabaseUserDetails user, String name, String description,
            String status,
            LocalDateTime createdAt, String serviceType, String code, Integer page) throws RoleInfoNotFoundException {
        Set<GrantedAuthority> roles = user.getAuthorities();
        Role emplyeeRole = roleRepo.findByName(RoleName.COMPANY_USER);
        Role customerRole = roleRepo.findByName(RoleName.CLIENT);

        Pageable pagination = PageRequest.of(page, 10);
        Specification<CompanyService> spec = Specification.<CompanyService>unrestricted()
                .and(nameContains(name))
                .and(serviceDescriptionContains(description))
                .and(hasServiceStatus(status))
                .and(ServiceCreatedAfter(createdAt))
                .and(hasServiceType(serviceType))
                .and(codeContains(code));

        GrantedAuthority employeeAuthority = new SimpleGrantedAuthority(emplyeeRole.toString());
        GrantedAuthority customerAuthority = new SimpleGrantedAuthority(customerRole.toString());

        Page<CompanyService> services = new PageImpl<>(new ArrayList<>());

        if (roles.isEmpty()) {
            throw new RoleInfoNotFoundException("Role not found");
        }

        if (roles.contains(employeeAuthority) && !roles.contains(customerAuthority)) {
            services = serviceRepo.findAll(spec.and(belongsToEmployee(user.getId())), pagination);
        } else if (roles.contains(customerAuthority) && !roles.contains(employeeAuthority)) {
            services = serviceRepo.findAll(spec.and(belongsToCustomer(user.getId())), pagination);
        }

        return services;
    }

    @Transactional
    public Page<CompanyServiceDTO> getAllFromUser(DatabaseUserDetails user, String name, String description,
            String status,
            LocalDateTime createdAt, String serviceType, String code, Integer page) throws AccessDeniedException {

        Pageable pagination = PageRequest.of(page, 10);
        Specification<CompanyService> spec = Specification.<CompanyService>unrestricted()
                .and(nameContains(name))
                .and(serviceDescriptionContains(description))
                .and(hasServiceStatus(status))
                .and(ServiceCreatedAfter(createdAt))
                .and(hasServiceType(serviceType))
                .and(codeContains(code));

        boolean isAdmin = user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()));
        boolean isEmployee = user.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()));
        boolean isCustomer = user.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()));
        Page<CompanyService> services = new PageImpl<>(new ArrayList<>());

        if (isAdmin) {
            services = serviceRepo.findAll(spec.and(belongsToCompany(user.getCompany().getId())),
                    pagination);
        } else if (isEmployee) {
            services = serviceRepo.findAll(spec.and(belongsToEmployee(user.getId())), pagination);
        } else if (isCustomer) {
            services = serviceRepo.findAll(spec.and(belongsToCustomer(user.getId())), pagination);
        } else {
            throw new AccessDeniedException("You don't have the authority to access this resource");
        }

        // conversione a dto
        return services.map(mapper::toCompanyServiceDTO);
    }

    public CompanyServiceDTO findById(Integer serviceId, DatabaseUserDetails user)
            throws AccessDeniedException, ServiceNotFoundException {
        Optional<CompanyService> service = serviceRepo.findById(serviceId);

        GrantedAuthority adminRole = new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString());
        GrantedAuthority employeeRole = new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString());
        GrantedAuthority customerRole = new SimpleGrantedAuthority(RoleName.CLIENT.toString());

        if (service.isEmpty()) {
            throw new ServiceNotFoundException("Serivce Not Found");
        }

        boolean userHasService = false;

        if (user.getAuthorities().contains(adminRole)) {
            userHasService = user.getCompany().getServices().stream()
                    .anyMatch(s -> s.getId().equals(service.get().getId()));
        } else if (user.getAuthorities().contains(employeeRole)) {
            userHasService = user.getServices().stream()
                    .anyMatch(s -> s.getId().equals(service.get().getId()));
        } else if (user.getAuthorities().contains(customerRole)) {
            userHasService = service.get().getCustomers().stream().anyMatch(c -> c.getId().equals(user.getId()));

        } else {
            throw new AccessDeniedException("User not authenticated");
        }

        if (!userHasService) {
            throw new AccessDeniedException("The current user is not registered to this service");
        }

        if (user.getAuthorities().contains(customerRole) && !user.getAuthorities().contains(adminRole)
                && !user.getAuthorities().contains(employeeRole)) {
            CompanyServiceDTO serviceDTO = mapper.toCompanyServiceDTO(service.get());
            serviceDTO.setCustomers(null);
            serviceDTO.setTickets(null);
            serviceDTO.setOperators(null);

            return serviceDTO;
        }

        return mapper.toCompanyServiceDTO(service.get());
    }

    public CompanyService findByIdEntity(Integer serviceId)
            throws ServiceNotFoundException {

        Optional<CompanyService> service = serviceRepo.findById(serviceId);

        if (service.isEmpty()) {
            throw new ServiceNotFoundException("Serivce Not Found");
        }
        return service.get();
    }

    @Transactional
    public CompanyServiceDTO store(CompanyServiceInputDTO service, DatabaseUserDetails user)
            throws IllegalArgumentException {

        // mappare da dto a entity
        CompanyService serviceEntity = mapper.toCompanyServiceEntity(service, serviceTypeRepo, ticketTypeRepo);

        // popolo campi mancanti
        serviceEntity.setCode(generateServiceCode());
        serviceEntity.setCompany(user.getCompany());
        serviceEntity.setStatus(ServiceStatus.INACTIVE);

        List<TicketType> ticketTypes = service.getTicketTypes().stream()
                .map(ticketTypeName -> {
                    TicketType t = new TicketType();
                    t.setName(ticketTypeName.get("name"));
                    t.setService(serviceEntity);
                    return t;
                })
                .toList();

        serviceEntity.getTicketTypes().clear();
        serviceEntity.getTicketTypes().addAll(ticketTypes);

        // salvare in db
        CompanyService saved = serviceRepo.save(serviceEntity);

        // ritornare DTO mappato
        return mapper.toCompanyServiceDTO(saved);
    }

    public CompanyServiceDTO update(CompanyServiceUpdateDTO serviceDto, Integer serviceId)
            throws IllegalArgumentException, ServiceNotFoundException {

        Optional<CompanyService> serviceEntity = serviceRepo.findById(serviceId);
        if (serviceEntity.isEmpty()) {
            throw new ServiceNotFoundException("Service not found");
        }

        CompanyService service = serviceEntity.get();

        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setServiceType(serviceTypeRepo.findById(serviceDto.getServiceTypeId()).get());
        List<TicketType> ticketTypeList = new ArrayList<>();
        // controllo se id e valorizzato
        for (TicketTypeDTO ttDto : serviceDto.getTicketTypes()) {
            if (ttDto.getId() == null) {
                // se si creo
                TicketType ticketType = new TicketType();
                ticketType.setName(ttDto.getName());
                ticketType.setService(service);
                ticketTypeRepo.save(ticketType);
                ticketTypeList.add(ticketType);

            } else {
                // se no aggiorno
                TicketType ttEntity = ticketTypeRepo.findById(ttDto.getId())
                        .orElseThrow(() -> new NotFoundException("Ticket Type not found"));

                ttEntity.setName(ttDto.getName());
                ticketTypeRepo.save(ttEntity);
                ticketTypeList.add(ttEntity);
            }
        }
        service.getTicketTypes().clear();
        service.getTicketTypes().addAll(ticketTypeList);
        service.setStatus(serviceDto.getStatus());

        serviceRepo.save(service);

        return mapper.toCompanyServiceDTO(service);

    }

    @Transactional
    public void deleteById(Integer id) throws ServiceNotFoundException {
        CompanyService serviceToDelete = serviceRepo.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));

        // rimuovo operators
        for (User operator : new ArrayList<>(serviceToDelete.getOperators())) {
            operator.getServices().remove(serviceToDelete);
        }
        serviceToDelete.getOperators().clear();

        for (User customer : new ArrayList<>(serviceToDelete.getCustomers())) {
            customer.getCustomerServices().remove(serviceToDelete);
        }
        serviceToDelete.getCustomers().clear();

        serviceToDelete.getTicketTypes().clear();
        serviceToDelete.getTickets().clear();

        serviceRepo.delete(serviceToDelete);

        em.flush();
        em.clear();

    }

    public Optional<CompanyService> findByTicketsContaining(Ticket ticket) {
        return serviceRepo.findByTicketsContaining(ticket);
    }
}
