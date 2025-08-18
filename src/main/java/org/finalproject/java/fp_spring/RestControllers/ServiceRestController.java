package org.finalproject.java.fp_spring.RestControllers;

import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.DTOs.CompanyServiceDTO;
import org.finalproject.java.fp_spring.Models.CompanyService;

import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.MapperService;
import org.finalproject.java.fp_spring.Services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceRestController {

    @Autowired
    ServiceService serviceService;

    @Autowired
    MapperService mapper;

    @GetMapping
    public ResponseEntity<?> index() {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal(); // cast

        List<CompanyServiceDTO> services = new ArrayList<>();

        for (CompanyService companyService : currentUser.getServices()) {
            services.add(mapper.toCompanyServiceDTO(companyService));
        }

        return ResponseEntity.ok(services);
    }

}
