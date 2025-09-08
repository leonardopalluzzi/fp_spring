package org.finalproject.java.fp_spring.Services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.DTOs.TicketHistoryDTO;
import org.finalproject.java.fp_spring.DTOs.TicketHistoryInputDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.TicketHistory;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.TicketHistroyRepository;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    TicketHistroyRepository ticketHistoryRepo;

    @Autowired
    MapperService mapper;

    public Page<TicketDTO> getOperatorPool(DatabaseUserDetails currentUser, String type, String status,
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

    public Page<TicketHistoryDTO> getHistory(Integer ticketId, DatabaseUserDetails currentUser, Integer page)
            throws UsernameNotFoundException, AccessDeniedException, NotFoundException {

        Ticket ticketEntity = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket Not Found"));

        User currentUserEntity = userSerivice.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isRelated = false;
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            isRelated = currentUserEntity.getCompany().getServices().stream()
                    .anyMatch(s -> s.getTickets().stream().anyMatch(t -> t.getId().equals(ticketId)));
        } else if (currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()))) {
            isRelated = currentUserEntity.getServices().stream().anyMatch(s -> s.getTickets().stream().anyMatch(t -> t.getId().equals(ticketId)));

        } else if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()))) {
            isRelated = currentUserEntity.getUserTickets().stream().anyMatch((t -> t.getId().equals(ticketId)));
        }

        if (!isRelated) {
            throw new AccessDeniedException("You don't have the authority to access this resource");
        }

        Pageable pagination = PageRequest.of(page, 10, Sort.by("changedAt").descending());
        Page<TicketHistory> ticketHistoryEntityPaged = ticketHistoryRepo.findAllByTicketId(ticketId, pagination);

        Page<TicketHistoryDTO> ticketHistoryDTOPaged = ticketHistoryEntityPaged.map(mapper::toTicketHistoryDTO);

        return ticketHistoryDTOPaged;
    }

    public void assignTicketToOperator(Integer operatorId, Integer ticketId, DatabaseUserDetails currentUser)
            throws AccessDeniedException, NotFoundException {

        boolean hasAuthority = false;

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            hasAuthority = currentUser.getCompany().getServices().stream()
                    .anyMatch(s -> s.getTickets().stream().anyMatch(t -> t.getId().equals(ticketId)));
        } else if (currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()))) {
            hasAuthority = currentUser.getServices().stream()
                    .anyMatch(s -> s.getOperators().stream().anyMatch(o -> o.getId().equals(currentUser.getId())));
        } else {
            throw new AccessDeniedException("You don't have the authority to complete this operation");
        }

        if (hasAuthority) {

            Ticket ticket = ticketRepo.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket Not Found"));
            if (operatorId == null) {
                ticket.setAssignedTo(null);
                ticketRepo.save(ticket);
                return;
            }
            User operator = userSerivice.getById(operatorId);

            // controllo se il ticket è gia assegnato a qualcuno ed in quel caso tolgo il
            // ticket dall'operatore
            if (ticket.getAssignedTo() != null) {
                ticket.setAssignedTo(null);
            }

            operator.getAdminTickets().add(ticket);
            ticket.setAssignedTo(operator);

            ticketRepo.save(ticket);

        } else {
            throw new AccessDeniedException("You don't have the authority to complete this operation");
        }
    }

    public void insertNotesIntoTicket(DatabaseUserDetails currentUser, TicketHistoryInputDTO ticketHistoryDTO)
            throws AccessDeniedException, NotFoundException {
        boolean hasAuthority = false;

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            hasAuthority = currentUser.getCompany().getServices().stream().anyMatch(
                    s -> s.getTickets().stream().anyMatch(t -> t.getId().equals(ticketHistoryDTO.getTicketId())));
        } else if (currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()))) {
            hasAuthority = currentUser.getServices().stream().anyMatch(
                    s -> s.getTickets().stream().anyMatch(t -> t.getId().equals(ticketHistoryDTO.getTicketId())));
        } else if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.CLIENT.toString()))) {
            User currentUserEntity = userSerivice.findById(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("User Not Found"));
            hasAuthority = currentUserEntity.getUserTickets().stream()
                    .anyMatch(t -> t.getId().equals(ticketHistoryDTO.getTicketId()));
        } else {
            throw new AccessDeniedException("User not logged");
        }

        if (hasAuthority) {

            // logica di save nel ticekthistory
            TicketHistory ticketHistoryEntity = new TicketHistory();
            ticketHistoryEntity.setChangedAt(ticketHistoryDTO.getChangedAt());
            ticketHistoryEntity.setChangedBy(userSerivice.findById(ticketHistoryDTO.getChangedById())
                    .orElseThrow(() -> new NotFoundException("Operator Not Found")));
            ticketHistoryEntity.setNotes(ticketHistoryDTO.getNotes());
            ticketHistoryEntity.setStatus(TicketStatus.valueOf(ticketHistoryDTO.getStatus().toUpperCase()));
            ticketHistoryEntity.setTicket(ticketRepo.findById(ticketHistoryDTO.getTicketId())
                    .orElseThrow(() -> new NotFoundException("Ticket Not Found")));

            Ticket ttToUpdate = ticketRepo.findById(ticketHistoryDTO.getTicketId())
                    .orElseThrow(() -> new NotFoundException("Ticket Not Found"));

            ttToUpdate.setStatus(ticketHistoryEntity.getStatus());

            ticketRepo.save(ttToUpdate);
            ticketHistoryRepo.save(ticketHistoryEntity);

        } else {
            throw new AccessDeniedException("You don't have the authority to complete this operation");
        }
    }

    public Page<TicketDTO> getTicketsByAllOperatorServices(DatabaseUserDetails currentUser, Integer page, String type, String status, String title, String description, LocalDateTime createdAt, Integer serviceId){
        Pageable pagination = PageRequest.of(page, 10);
        Specification<Ticket> spec = Specification.<Ticket>unrestricted()
                                        .and(hasType(type))
                                        .and(hasStatus(status))
                                        .and(titleContains(title))
                                        .and(descriptionContains(description))
                                        .and(createdAfter(createdAt))
                                        .and(belongsToService(serviceId))
                                        .and(belongsToOperatorService(currentUser.getId()));
        Page<Ticket> ttListDTO = ticketRepo.findAll(spec, pagination);
        return ttListDTO.map(t -> mapper.toTicketDTO(t));
    }

}
