package org.finalproject.java.fp_spring.RestControllers;

import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.DTOs.ServiceTypeDTO;
import org.finalproject.java.fp_spring.Models.ServiceType;
import org.finalproject.java.fp_spring.Repositories.ServiceTypeRepository;
import org.finalproject.java.fp_spring.Services.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tipologies")
public class TipologyRestController {

    @Autowired
    ServiceTypeRepository serviceTypeRepo;

    @Autowired
    MapperService mapper;

    @GetMapping("/servicetypes")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN', 'ADMIN')")
    public ResponseEntity<?> getServiceTypes() {
        List<ServiceType> serviceTypes = serviceTypeRepo.findAll();
        List<ServiceTypeDTO> serviceTypesDTO = new ArrayList<>();
        for (ServiceType serviceType : serviceTypes) {
            ServiceTypeDTO dto = new ServiceTypeDTO();
            dto.setId(serviceType.getId());
            dto.setName(serviceType.getName());

            serviceTypesDTO.add(dto);
        }
        return ResponseEntity.ok(serviceTypesDTO);
    }

}
