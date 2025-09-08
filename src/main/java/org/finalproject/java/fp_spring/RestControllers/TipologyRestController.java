package org.finalproject.java.fp_spring.RestControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.finalproject.java.fp_spring.DTOs.ServiceTypeDTO;
import org.finalproject.java.fp_spring.DTOs.TicketTypeDTO;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.ServiceType;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
import org.finalproject.java.fp_spring.Services.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/servicetypes")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN', 'ADMIN', 'COMPANY_USER')")
    public ResponseEntity<?> getServiceTypes() {
        try {
            List<ServiceType> serviceTypes = serviceTypeRepo.findAll();
            List<ServiceTypeDTO> serviceTypesDTO = new ArrayList<>();
            for (ServiceType serviceType : serviceTypes) {
                ServiceTypeDTO dto = new ServiceTypeDTO();
                dto.setId(serviceType.getId());
                dto.setName(serviceType.getName());

                serviceTypesDTO.add(dto);
            }
            return ResponseEntity.ok(Map.of("state", "success", "result", serviceTypesDTO));

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "ecpired", "message", e.getMessage()));
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
