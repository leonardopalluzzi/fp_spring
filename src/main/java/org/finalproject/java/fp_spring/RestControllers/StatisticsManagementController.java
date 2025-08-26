package org.finalproject.java.fp_spring.RestControllers;

import java.util.Map;

import org.finalproject.java.fp_spring.DTOs.CompanyStatsDTO;
import org.finalproject.java.fp_spring.DTOs.CustomerStatsDTO;
import org.finalproject.java.fp_spring.DTOs.EmployeeStatsDTO;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.StatisticsManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
public class StatisticsManagementController {

    @Autowired
    StatisticsManagementService statsService;

    @GetMapping("/company")
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<?> companyStats() {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            CompanyStatsDTO companyStats = statsService.getCompanyStats(currentUser);
            return ResponseEntity.ok(companyStats);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }

    }

    @GetMapping("/employee")
    @PreAuthorize("hasAuthority('COMPANY_USER')")
    public ResponseEntity<?> employeeStats() {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            EmployeeStatsDTO employeeStats = statsService.getEmployeeStats(currentUser);
            return ResponseEntity.ok(employeeStats);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }

    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> custmoerStats() {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            CustomerStatsDTO customerStast = statsService.getCustomerStats(currentUser);
            return ResponseEntity.ok(customerStast);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }

    }

}
