package org.finalproject.java.fp_spring.RestControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.finalproject.java.fp_spring.DTOs.ServiceTypeDTO;
import org.finalproject.java.fp_spring.DTOs.TicketTypeDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.ServiceType;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtException;

@RestController
@RequestMapping("/api/v1/tipologies")
public class TipologyRestController {

    @Autowired
    ServiceTypeRepository serviceTypeRepo;

    @Autowired
    TicketTypeRepository ticketTypeRepo;

    @Autowired
    ServiceRepository serviceRepo;

    @Autowired
    TicketsRepository ticketsRepo;

    @Autowired
    MapperService mapper;

    @Autowired
    UserRepository userRepo;

    @GetMapping("/getAllServiceTypes")
    public ResponseEntity<?> getAllServiceTypesForCreate() {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userRepo.findById(currentUser.getId()).orElseThrow(() -> new NotFoundException("User not found"));
        List<ServiceType> serviceTypes = new ArrayList<>();
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            serviceTypes = serviceTypeRepo.findAll();

            List<ServiceTypeDTO> dtos = new ArrayList<>();
            for (ServiceType entity : serviceTypes) {
                dtos.add(mapper.toServiceTypeDTO(entity));
            }

            return ResponseEntity.ok(Map.of("state", "success", "result", dtos));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "error", "message", "You don't have the uthority to access this resource"));
        }

    }

    @GetMapping("/servicetypes")
    public ResponseEntity<?> getServiceTypes() {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userRepo.findById(currentUser.getId()).orElseThrow(() -> new NotFoundException("User not found"));
        try {

            List<ServiceType> serviceTypes = new ArrayList<>();

            if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
                serviceTypes = user.getCompany().getServices().stream().map(CompanyService::getServiceType)
                        .distinct().collect(Collectors.toList());
            }
            if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()))) {
                serviceTypes = user.getServices().stream().map(CompanyService::getServiceType).distinct()
                        .collect(Collectors.toList());
            }
            // se sono customer, tutti i type presi dalla lista customerservice dello user
            // presi una sola volta
            if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()))) {
                // popolo lista
                serviceTypes = user.getCustomerServices().stream().map(CompanyService::getServiceType).distinct()
                        .collect(Collectors.toList());
            }

            // mappo a dto e restituisco
            List<ServiceTypeDTO> serviceTypesDTO = new ArrayList<>();
            for (ServiceType serviceType : serviceTypes) {
                ServiceTypeDTO dto = new ServiceTypeDTO();
                dto.setId(serviceType.getId());
                dto.setName(serviceType.getName());

                serviceTypesDTO.add(dto);
            }
            return ResponseEntity.ok(Map.of("state", "success", "result", serviceTypesDTO));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "expired", "message", e.getMessage()));
        }
    }

    @GetMapping("/tickettypes/{id}")
    public ResponseEntity<?> getTicketTypes(@PathVariable("id") Integer serviceId) {

        try {
            CompanyService service = serviceRepo.findById(serviceId)
                    .orElseThrow(() -> new NotFoundException("Service Not Found"));

            List<TicketType> ticketTypesEntity = service.getTicketTypes();
            List<TicketTypeDTO> ticketTypes = new ArrayList<>();

            for (TicketType entity : ticketTypesEntity) {
                ticketTypes.add(mapper.toTicketTypeDTO(entity));
            }

            return ResponseEntity.ok(Map.of("state", "success", "result", ticketTypes));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "ecpired", "message", e.getMessage()));
        }
    }
}
