package org.finalproject.java.fp_spring.Services;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import javax.management.ServiceNotFoundException;

import org.apache.coyote.BadRequestException;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceDTO;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceLightDTO;
import org.finalproject.java.fp_spring.DTOs.CustomerRegisterRequestDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class ServiceManagementService {

    @Autowired
    ServiceService serviceService;

    @Autowired
    ServiceRepository serviceRepo;

    @Autowired
    UserService userService;

    @Autowired
    MapperService mapper;

    public void assignOperatorToService(Integer serviceId, Integer userId, DatabaseUserDetails currentUser)
            throws AccessDeniedException, NotFoundException, BadRequestException, ServiceNotFoundException {

        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            throw new AccessDeniedException("You don't have the authority to access this resource");
        }

        try {

            // recupero service
            CompanyServiceDTO serivceDTO = serviceService.findById(serviceId, currentUser);
            CompanyService serviceEntity = serviceRepo.findById(serivceDTO.getId())
                    .orElseThrow(() -> new ServiceNotFoundException("Service Not Found"));

            // recupero user
            User userEntity = userService.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));

            // verifico che lo user non e gia nel service
            if (serviceEntity.getOperators().stream().anyMatch(s -> s.getId().equals(userId))) {
                throw new BadRequestException("The User is already assigned to this service");
            }

            // verifico se richiedente e relazionato al service
            boolean isRelated = currentUser.getCompany().getServices().stream()
                    .anyMatch(s -> s.getId().equals(serviceId));

            // se ok aggiungo relazione
            if (isRelated) {
                serviceEntity.getOperators().add(userEntity);
                serviceRepo.save(serviceEntity);
            } else {
                throw new AccessDeniedException("You don't have the authority to access this resource");
            }

        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (ServiceNotFoundException e) {
            throw new ServiceNotFoundException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void detachOperatorFromService(Integer serviceId, Integer userId, DatabaseUserDetails currentUser)
            throws AccessDeniedException, NotFoundException, BadRequestException, ServiceNotFoundException {

        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            throw new AccessDeniedException("You don't have the authority to access this resource");
        }

        try {

            // recupero service
            CompanyServiceDTO serivceDTO = serviceService.findById(serviceId, currentUser);
            CompanyService serviceEntity = serviceRepo.findById(serivceDTO.getId())
                    .orElseThrow(() -> new ServiceNotFoundException("Service Not Found"));

            // recupero user
            User userEntity = userService.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));

            // verifico che lo user non e gia nel service
            if (!serviceEntity.getOperators().stream().anyMatch(s -> s.getId().equals(userId))) {
                throw new BadRequestException("The User is already removed from this service");
            }

            // verifico se richiedente e relazionato al service
            boolean isRelated = currentUser.getCompany().getServices().stream()
                    .anyMatch(s -> s.getId().equals(serviceId));

            // se ok aggiungo relazione
            if (isRelated) {
                serviceEntity.getOperators().remove(userEntity);
                userEntity.getServices().remove(serviceEntity);
                serviceRepo.save(serviceEntity);
            } else {
                throw new AccessDeniedException("You don't have the authority to access this resource");
            }

        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (ServiceNotFoundException e) {
            throw new ServiceNotFoundException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    public void detachCustomerFromService(Integer serviceId, Integer userId, DatabaseUserDetails currentUser)
            throws AccessDeniedException, NotFoundException, ServiceNotFoundException {

        boolean isAdmin = currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()));
        boolean isCustomer = currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()));

        if (isCustomer) {
            // verifico che userId == currentUser.id
            if (currentUser.getId() != userId) {
                throw new AccessDeniedException("You don't have the authority to access this resource");
            }

        } else if (isAdmin) {
            if (!currentUser.getCompany().getServices().stream().anyMatch(s -> s.getId().equals(serviceId))) {
                throw new AccessDeniedException("You don't have the authority to access this resource");
            }

        } else {
            throw new AccessDeniedException("User Not Allowed Here");
        }

        try {
            // trovo service
            CompanyServiceDTO serviceDTO = serviceService.findById(serviceId, currentUser);
            CompanyService serviceEntity = serviceRepo.findById(serviceDTO.getId())
                    .orElseThrow(() -> new ServiceNotFoundException("Service Not Found"));

            // trovo user
            User userEntity = userService.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));

            // verifico relazione dal service trovato
            boolean isRelated = serviceEntity.getCustomers().stream()
                    .anyMatch(c -> c.getId().equals(userEntity.getId()));

            if (isRelated) {
                // se ok rimuovo user dal service
                serviceEntity.getCustomers().remove(userEntity);
                // rimuovo service dallo user
                userEntity.getServices().remove(serviceEntity);
                serviceRepo.save(serviceEntity);

            } else {
                throw new AccessDeniedException("You don't have the authority to access this resource");
            }

        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ServiceNotFoundException e) {
            throw new ServiceNotFoundException(e.getMessage());
        }

    }

    public void registerCustomerToService(DatabaseUserDetails currentUser, CustomerRegisterRequestDTO request)
            throws AccessDeniedException, ServiceNotFoundException, BadRequestException {
        try {
            // verificare che il current user corrisponda allo user id
            if (!currentUser.getId().equals(request.getUserId())) {
                throw new AccessDeniedException("You don't have the authority to access this resource");
            }

            // verificare che il service esista
            CompanyService serviceEntity = serviceRepo.findById(request.getServiceId())
                    .orElseThrow(() -> new ServiceNotFoundException("Service Not Found"));

            // verificare che lo user non sia gia registrato
            if (serviceEntity.getCustomers().stream().anyMatch(c -> c.getId().equals(request.getUserId()))) {
                throw new BadRequestException("The user is already registered to this service");
            }

            // conforntare codice service con codice requeset
            boolean isMatch = serviceEntity.getCode().equals(request.getServiceCode());

            // se codici corrispondono aggiungere customer a service
            if (isMatch) {
                User userToAssign = userService.getById(request.getUserId());
                serviceEntity.getCustomers().add(userToAssign);
                userToAssign.getServices().add(serviceEntity);

                serviceRepo.save(serviceEntity);
            } else {
                throw new BadRequestException("The service code is not valid for the required service");
            }

        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ServiceNotFoundException e) {
            throw new ServiceNotFoundException(e.getMessage());
        }

    }

    public List<CompanyServiceLightDTO> getAll(DatabaseUserDetails currentUser) {
        List<CompanyService> services = serviceRepo.findAllByCompanyId(currentUser.getCompany().getId());

        List<CompanyServiceLightDTO> servicesDTO = new ArrayList<>();

        for (CompanyService entity : services) {
            servicesDTO.add(mapper.toCompanyServiceLightDTO(entity));
        }

        return servicesDTO;
    }
}
