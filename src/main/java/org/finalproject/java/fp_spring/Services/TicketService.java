package org.finalproject.java.fp_spring.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Specifications.TicketsSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    MapperService mapper;

    @Autowired
    TicketsRepository ticketsRepo;

    @Autowired
    CompanyService companyService;

    public Page<TicketDTO> findCustomerTicketsFiltered(DatabaseUserDetails user, TicketType type, TicketStatus status,
            String title, String description, LocalDateTime createdAt, Integer page) {

        // prendo i ticket da db gia filtrati per l'utente
        User currentUser = userService.getById(user.getId());
        Pageable pagination = PageRequest.of(page, 10);

        Specification<Ticket> spec = Specification
                .where(TicketsSpecifications.belongsToRequester(currentUser))
                .and(TicketsSpecifications.hasType(type))
                .and(TicketsSpecifications.hasStatus(status))
                .and(TicketsSpecifications.titleContains(title))
                .and(TicketsSpecifications.descriptionContains(description))
                .and(TicketsSpecifications.createdAfter(createdAt));

        Page<Ticket> ticketsEntity = ticketsRepo.findAll(spec, pagination);

        Page<TicketDTO> ticketsDTO = ticketsEntity.map(ticket -> mapper.toTicketDTO(ticket));

        return ticketsDTO;

    }

    public Page<TicketDTO> findEmployeeTicketFiltered(DatabaseUserDetails user, TicketType type, TicketStatus status,
            String title, String description, LocalDateTime createdAt, Integer page) {
        // prendo tutti i ticket assegnati all'impiegato
        User currentUser = userService.getById(user.getId());
        Pageable pagination = PageRequest.of(page, 10);

        Specification<Ticket> spec = Specification
                .where(TicketsSpecifications.belongsToAssignee(currentUser))
                .and(TicketsSpecifications.hasType(type))
                .and(TicketsSpecifications.hasStatus(status))
                .and(TicketsSpecifications.titleContains(title))
                .and(TicketsSpecifications.descriptionContains(description))
                .and(TicketsSpecifications.createdAfter(createdAt));

        Page<Ticket> ticketsEntity = ticketsRepo.findAll(spec, pagination);

        Page<TicketDTO> ticketsDTO = ticketsEntity.map(ticket -> mapper.toTicketDTO(ticket));

        return ticketsDTO;

    }

    public Page<TicketDTO> findCompanyTicketFiltered(DatabaseUserDetails user, TicketType type, TicketStatus status,
            String title, String description, LocalDateTime createdAt, Integer page) {
        // prendo tutti i ticket per la compangia
        User currentUser = userService.getById(user.getId());
        Integer companyId = currentUser.getCompany().getId();
        Pageable pagination = PageRequest.of(page, 10);

        Specification<Ticket> spec = Specification
                .where(TicketsSpecifications.belongsToCompany(companyId))
                .and(TicketsSpecifications.hasType(type))
                .and(TicketsSpecifications.hasStatus(status))
                .and(TicketsSpecifications.titleContains(title))
                .and(TicketsSpecifications.descriptionContains(description))
                .and(TicketsSpecifications.createdAfter(createdAt));

        Page<Ticket> ticketsEntity = ticketsRepo.findAll(spec, pagination);

        Page<TicketDTO> ticketsDTO = ticketsEntity.map(ticket -> mapper.toTicketDTO(ticket));

        return ticketsDTO;
    }

}
