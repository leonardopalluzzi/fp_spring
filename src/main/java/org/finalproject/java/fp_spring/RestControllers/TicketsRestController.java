package org.finalproject.java.fp_spring.RestControllers;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import javax.management.ServiceNotFoundException;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.DTOs.TicketInputDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketsRestController {

    @Autowired
    TicketService ticketService;

    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam(name = "type", required = false) TicketType type,
            @RequestParam(name = "status", required = false) TicketStatus status,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "createdAt", required = false) LocalDateTime createdAt,
            @RequestParam(name = "page", required = true, defaultValue = "0") Integer page,
            @RequestParam(name = "serviceId", required = false) Integer serviceId) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged");
        }

        GrantedAuthority customerRole = new SimpleGrantedAuthority(RoleName.CLIENT.toString());
        GrantedAuthority employeeRole = new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString());
        GrantedAuthority companyAdminRole = new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString());

        Page<TicketDTO> tickets = Page.empty();

        if (currentUser.getAuthorities().contains(customerRole)) {
            tickets = ticketService.findCustomerTicketsFiltered(currentUser, type, status, title, description,
                    createdAt, page, serviceId);

        } else if (currentUser.getAuthorities().contains(employeeRole)) {
            tickets = ticketService.findEmployeeTicketFiltered(currentUser, type, status, title, description,
                    createdAt, page, serviceId);

        } else if (currentUser.getAuthorities().contains(companyAdminRole)) {
            tickets = ticketService.findCompanyTicketFiltered(currentUser, type, status, title, description,
                    createdAt, page, serviceId);
        }

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable("id") Integer id) {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            TicketDTO ticket = ticketService.findByIdFilterByRole(currentUser, id);

            return ResponseEntity.ok(ticket);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }

    }

    @PostMapping("/store/{id}")
    public ResponseEntity<?> store(@Valid @RequestBody TicketInputDTO ticket, BindingResult bindingResult,
            @PathVariable("id") Integer id) {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("There are errors in some fields");
        }

        try {
            TicketDTO storedTicket = ticketService.store(ticket, currentUser, id);
            return ResponseEntity.ok(storedTicket);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }

    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody TicketInputDTO ticket) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            TicketDTO updatedTicket = ticketService.update(ticket, currentUser);
            return ResponseEntity.ok(updatedTicket);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            ticketService.deleteById(id, currentUser);
            return ResponseEntity.ok().build();

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        }

    }

}
