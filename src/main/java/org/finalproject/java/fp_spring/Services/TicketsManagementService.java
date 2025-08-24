package org.finalproject.java.fp_spring.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.finalproject.java.fp_spring.Specifications.TicketsSpecifications.*;

@Service
public class TicketsManagementService {

    @Autowired
    UserService userSerivice;

    @Autowired
    TicketsRepository ticketRepo;

    @Autowired
    MapperService mapper;

    public Page<TicketDTO> getOperatorPool(DatabaseUserDetails currentUser, TicketType type, TicketStatus status,
            String title,
            String description,
            LocalDateTime createdAt, Integer page, Integer serviceId) throws UsernameNotFoundException {
        User userEntity = userSerivice.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Pageable pagiantion = PageRequest.of(page, 10);

        Specification<Ticket> spec = Specification.<Ticket>unrestricted()
                .and(belongsToOperatorService(currentUser.getId()))
                .and(hasNoAssignee()) // servono per ottenere la pull di ticket relazionati ai servizi dello user
                                      // corrente (operatore) che non hanno assegnatario
                .and(hasType(type))
                .and(hasStatus(status))
                .and(titleContains(title))
                .and(descriptionContains(description))
                .and(createdAfter(createdAt))
                .and(belongsToService(serviceId));

        Page<Ticket> ticketsEntity = ticketRepo.findAll(spec, pagiantion);

        List<Ticket> ticketsEntityList = ticketsEntity.getContent();
        List<TicketDTO> ticketsDTOList = new ArrayList<>();

        for (Ticket ticket : ticketsEntityList) {
            ticketsDTOList.add(mapper.toTicketDTO(ticket));
        }

        Page<TicketDTO> ticketsDTO = new PageImpl<TicketDTO>(ticketsDTOList);

        return ticketsDTO;
    }

}
