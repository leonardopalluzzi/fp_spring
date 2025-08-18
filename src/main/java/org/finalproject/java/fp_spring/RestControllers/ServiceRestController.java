package org.finalproject.java.fp_spring.RestControllers;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import javax.management.ServiceNotFoundException;

import org.finalproject.java.fp_spring.DTOs.CompanyServiceDTO;
import org.finalproject.java.fp_spring.Models.CompanyService;

import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.MapperService;
import org.finalproject.java.fp_spring.Services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceRestController {

    @Autowired
    ServiceService serviceService;

    @GetMapping
    public ResponseEntity<?> index() {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal(); // cast

        List<CompanyServiceDTO> services = serviceService.getAllFromUser(currentUser);

        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Integer id) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            CompanyServiceDTO service = serviceService.findById(id, currentUser);
            return ResponseEntity.ok(service);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
