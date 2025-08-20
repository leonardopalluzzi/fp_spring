package org.finalproject.java.fp_spring.RestControllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketsRestController {

    @Autowired
    TicketService ticketService;

    @GetMapping
    public ResponseEntity<Page<TicketDTO>> index(
            @RequestParam(name = "type", required = false) TicketType type,
            @RequestParam(name = "status", required = false) TicketStatus status,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "createdAt", required = false) LocalDateTime createdAt,
            @RequestParam(name = "page", required = true, defaultValue = "0") Integer page) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        GrantedAuthority customerRole = new SimpleGrantedAuthority(RoleName.CLIENT.toString());
        GrantedAuthority employeeRole = new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString());
        GrantedAuthority companyAdminRole = new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString());

        Page<TicketDTO> tickets = Page.empty();

        if (currentUser.getAuthorities().contains(customerRole)) {
            tickets = ticketService.findCustomerTicketsFiltered(currentUser, type, status, title, description,
                    createdAt, page);

        } else if (currentUser.getAuthorities().contains(employeeRole)) {
            tickets = ticketService.findEmployeeTicketFiltered(currentUser, type, status, title, description,
                    createdAt, page);

        } else if (currentUser.getAuthorities().contains(companyAdminRole)) {
            tickets = ticketService.findCompanyTicketFiltered(currentUser, type, status, title, description,
                    createdAt, page);
        }

        return ResponseEntity.ok(tickets);
    }

}
