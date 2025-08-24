package org.finalproject.java.fp_spring.RestControllers;

import java.time.LocalDateTime;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.TicketsManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets/manage")
public class TicketsManagementController {

    @Autowired
    TicketsManagementService ticketsManagementService;

    // get pool di ticket da assengare
    @GetMapping
    public ResponseEntity<?> getPool(
            @RequestParam(name = "type", required = false) TicketType type,
            @RequestParam(name = "status", required = false) TicketStatus status,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "createdAt", required = false) LocalDateTime createdAt,
            @RequestParam(name = "page", required = true, defaultValue = "0") Integer page,
            @RequestParam(name = "serviceId", required = false) Integer serviceId) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            Page<TicketDTO> tickets = ticketsManagementService.getOperatorPool(currentUser, type, status, title,
                    description,
                    createdAt, page, serviceId);

            return ResponseEntity.ok(tickets);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    // get storico ticket

}
