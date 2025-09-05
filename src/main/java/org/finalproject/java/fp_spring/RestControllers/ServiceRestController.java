package org.finalproject.java.fp_spring.RestControllers;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.management.ServiceNotFoundException;

import org.finalproject.java.fp_spring.DTOs.CompanyServiceDTO;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceInputDTO;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceRestController {

    @Autowired
    ServiceService serviceService;

    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "createdAt", required = false) LocalDateTime createdAt,
            @RequestParam(name = "serviceType", required = false) String serviceType,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "page", required = true) Integer page) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal(); // cast

        try {
            Page<CompanyServiceDTO> services = serviceService.getAllFromUser(currentUser, name, description, status,
                    createdAt, serviceType, code, page);

            return ResponseEntity.ok(Map.of("state", "success", "result", services));

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Integer id) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            CompanyServiceDTO service = serviceService.findById(id, currentUser);
            return ResponseEntity.ok(Map.of("state", "success", "result", service));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("state", "error", "message", e.getMessage()));
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("state", "error", "message", e.getMessage()));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("state", "expired", "message", e.getMessage()));
        }

    }

    @PostMapping("/store")
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<?> store(@Valid @RequestBody CompanyServiceInputDTO service,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            CompanyServiceDTO saved = serviceService.store(service, currentUser);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody CompanyServiceInputDTO serviceDto,
            BindingResult bindingResult, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("There are errors in some fields");
        }

        try {
            CompanyServiceDTO updated = serviceService.update(serviceDto, id);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        }

    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {

        try {
            serviceService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Item deleted correctly", "status", "OK"));

        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        }
    }

}
