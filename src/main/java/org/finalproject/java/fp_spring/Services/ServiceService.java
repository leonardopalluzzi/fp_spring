package org.finalproject.java.fp_spring.Services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.management.ServiceNotFoundException;
import javax.management.relation.RoleInfoNotFoundException;

import org.finalproject.java.fp_spring.DTOs.CompanyServiceDTO;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceInputDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Enum.ServiceStatus;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketTypeRepository;
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

    @Autowired
    private MapperService mapper;

    @Autowired
    ServiceTypeRepository serviceTypeRepo;

    @Autowired
    TicketTypeRepository ticketTypeRepo;

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

    public List<CompanyServiceDTO> getAllFromUser(DatabaseUserDetails user) {

        boolean isAdmin = user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()));
        boolean isEmployee = user.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()));
        boolean isCustomer = user.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()));
        List<CompanyServiceDTO> services = new ArrayList<>();

        if (isAdmin) {
            for (CompanyService companyService : user.getCompany().getServices()) {
                services.add(mapper.toCompanyServiceDTO(companyService));
            }
        } else if (isEmployee) {
            for (CompanyService companyService : user.getServices()) {
                services.add(mapper.toCompanyServiceDTO(companyService));
            }
        } else if (isCustomer) {
            List<CompanyService> servicesEntity = serviceRepo.findByCustomers_id(user.getId());
            for (CompanyService entity : servicesEntity) {
                services.add(mapper.toCompanyServiceDTO(entity));
            }
        }

        return services;
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

    public CompanyServiceDTO store(CompanyServiceInputDTO service, DatabaseUserDetails user)
            throws IllegalArgumentException {

        // mappare da dto a entity
        CompanyService serviceEntity = mapper.toCompanyServiceEntity(service, serviceTypeRepo, ticketTypeRepo);

        // popolo campi mancanti
        serviceEntity.setCode(generateServiceCode());
        serviceEntity.setCompany(user.getCompany());
        serviceEntity.setStatus(ServiceStatus.INACTIVE);
        List<TicketType> ticketTypeList = new ArrayList<>();
        for (String ticketTypeDTO : service.getTicketType()) {

            TicketType ticketType = new TicketType();
            ticketType.setName(ticketTypeDTO);
            ticketType.setService(serviceEntity);
            ticketTypeList.add(ticketType);
        }
        serviceEntity.getTicketTypes().clear();
        serviceEntity.getTicketTypes().addAll(ticketTypeList);

        // salvare in db
        CompanyService saved = serviceRepo.save(serviceEntity);

        // ritornare DTO mappato
        return mapper.toCompanyServiceDTO(saved);
    }

    public CompanyServiceDTO update(CompanyServiceInputDTO serviceDto, Integer serviceId)
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
        for (String ticketTypeDTO : serviceDto.getTicketType()) {

            TicketType ticketType = new TicketType();
            ticketType.setName(ticketTypeDTO);
            ticketType.setService(service);
            ticketTypeList.add(ticketType);
        }
        service.getTicketTypes().clear();
        service.getTicketTypes().addAll(ticketTypeList);
        service.setStatus(serviceDto.getStatus());

        serviceRepo.save(service);

        return mapper.toCompanyServiceDTO(service);

    }

    public void deleteById(Integer id) throws ServiceNotFoundException {
        Optional<CompanyService> serviceToDelete = serviceRepo.findById(id);
        if (serviceToDelete.isEmpty()) {
            throw new ServiceNotFoundException("Service not found");
        }
        serviceRepo.delete(serviceToDelete.get());
    }

    public Optional<CompanyService> findByTicketsContaining(Ticket ticket) {
        return serviceRepo.findByTicketsContaining(ticket);
    }
}
