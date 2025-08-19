package org.finalproject.java.fp_spring.RestControllers;

import java.util.List;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/v1/tickets")
public class TicketsRestController {

    @Autowired
    TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketDTO>> index() {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            List<TicketDTO> tickets = ticketService.findAllByUser(currentUser);
            return ResponseEntity.ok(tickets);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
