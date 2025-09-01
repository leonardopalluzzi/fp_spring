package org.finalproject.java.fp_spring.Services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.management.ServiceNotFoundException;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.DTOs.TicketInputDTO;
import org.finalproject.java.fp_spring.DTOs.TicketLightInputDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.TicketHistory;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.AttachmentRepository;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Repositories.ServiceTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketHistroyRepository;
import org.finalproject.java.fp_spring.Repositories.TicketTypeRepository;
import org.finalproject.java.fp_spring.Repositories.TicketsRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.finalproject.java.fp_spring.Specifications.TicketsSpecifications.*;

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

    @Autowired
    ServiceService serviceService;

    @Autowired
    ServiceTypeRepository serviceTypeRepo;

    @Autowired
    TicketTypeRepository ticketTypeRepo;

    @Autowired
    AttachmentRepository attachmentRepo;

    @Autowired
    ServiceRepository serviceRepo;

    @Autowired
    TicketHistroyRepository ticketHistoryRepo;

    public Page<TicketDTO> findCustomerTicketsFiltered(DatabaseUserDetails user, String type, String status,
            String title, String description, LocalDateTime createdAt, Integer page, Integer serviceId) {

        // prendo i ticket da db gia filtrati per l'utente
        User currentUser = userService.getById(user.getId());
        Pageable pagination = PageRequest.of(page, 10);

        Specification<Ticket> spec = Specification.<Ticket>unrestricted()
                .and(belongsToRequester(currentUser))
                .and(hasType(type))
                .and(hasStatus(status))
                .and(titleContains(title))
                .and(descriptionContains(description))
                .and(createdAfter(createdAt))
                .and(belongsToService(serviceId));

        Page<Ticket> ticketsEntity = ticketsRepo.findAll(spec, pagination);

        Page<TicketDTO> ticketsDTO = ticketsEntity.map(ticket -> mapper.toTicketDTO(ticket));

        return ticketsDTO;

    }

    public Page<TicketDTO> findEmployeeTicketFiltered(DatabaseUserDetails user, String type, String status,
            String title, String description, LocalDateTime createdAt, Integer page, Integer serviceId) {
        // prendo tutti i ticket assegnati all'impiegato
        User currentUser = userService.getById(user.getId());
        Pageable pagination = PageRequest.of(page, 10);

        Specification<Ticket> spec = Specification.<Ticket>unrestricted()
                .and(belongsToRequester(currentUser))
                .and(hasType(type))
                .and(hasStatus(status))
                .and(titleContains(title))
                .and(descriptionContains(description))
                .and(createdAfter(createdAt))
                .and(belongsToService(serviceId));

        Page<Ticket> ticketsEntity = ticketsRepo.findAll(spec, pagination);

        Page<TicketDTO> ticketsDTO = ticketsEntity.map(ticket -> mapper.toTicketDTO(ticket));

        return ticketsDTO;

    }

    public Page<TicketDTO> findCompanyTicketFiltered(DatabaseUserDetails user, String type, String status,
            String title, String description, LocalDateTime createdAt, Integer page, Integer serviceId) {
        // prendo tutti i ticket per la compangia
        User currentUser = userService.getById(user.getId());
        Integer companyId = currentUser.getCompany().getId();
        Pageable pagination = PageRequest.of(page, 10);

        Specification<Ticket> spec = Specification.<Ticket>unrestricted()
                .and(belongsToRequester(currentUser))
                .and(hasType(type))
                .and(hasStatus(status))
                .and(titleContains(title))
                .and(descriptionContains(description))
                .and(createdAfter(createdAt))
                .and(belongsToService(serviceId));

        Page<Ticket> ticketsEntity = ticketsRepo.findAll(spec, pagination);

        Page<TicketDTO> ticketsDTO = ticketsEntity.map(ticket -> mapper.toTicketDTO(ticket));

        return ticketsDTO;
    }

    public TicketDTO findByIdFilterByRole(DatabaseUserDetails user, Integer ticketId) throws NotFoundException {
        Optional<Ticket> ticket = ticketsRepo.findById(ticketId);

        if (ticket.isEmpty()) {
            throw new NotFoundException("Ticket not found");
        }

        // gestire ruoli

        return mapper.toTicketDTO(ticket.get());

    }

    public TicketDTO store(TicketInputDTO ticket, DatabaseUserDetails user, Integer serviceId)
            throws AccessDeniedException, ServiceNotFoundException, NotFoundException {
        User currentUser = userService.getById(user.getId());

        Role adminRole = roleRepo.findByName(RoleName.COMPANY_ADMIN);
        Role employeeRole = roleRepo.findByName(RoleName.COMPANY_USER);
        Role customerRole = roleRepo.findByName(RoleName.CLIENT);

        try {

            org.finalproject.java.fp_spring.Models.CompanyService serviceEntity = serviceRepo.findById(serviceId)
                    .orElseThrow(() -> new ServiceNotFoundException("Service Not Found"));

            Ticket ticketToSave = new Ticket();

            // setto le relazioni e le info automatiche in base al ruolo
            if (currentUser.getRoles().contains(adminRole)) {
                boolean isRelated = user.getCompany().getServices().stream().anyMatch(s -> s.getId().equals(serviceId));
                if (!isRelated) {
                    throw new AccessDeniedException("You don't have te authority to access this resource company");
                }
                ticketToSave.setAssignedTo(currentUser);
                ticketToSave.setRequester(currentUser);

            } else if (currentUser.getRoles().contains(employeeRole)) {
                boolean isRelated = serviceEntity.getOperators().stream().anyMatch(o -> o.getId().equals(user.getId()));
                if (!isRelated) {
                    throw new AccessDeniedException("You don't have te authority to access this resource employee");
                }
                ticketToSave.setAssignedTo(currentUser);
                ticketToSave.setRequester(currentUser);

            } else if (currentUser.getRoles().contains(customerRole)) {
                boolean isRelated = serviceEntity.getCustomers().stream()
                        .anyMatch(c -> c.getId().equals(currentUser.getId()));
                if (!isRelated) {
                    throw new AccessDeniedException("You don't have te authority to access this resource customer");
                }
                ticketToSave.setRequester(currentUser);
            }

            ticketToSave.setCreatedAt(LocalDateTime.now());
            ticketToSave.setService(serviceEntity);

            // info che arrivano dall'input dto
            ticketToSave.setTitle(ticket.getTitle());
            ticketToSave.setAttachments(ticket.getAttachments());
            TicketType type = ticketTypeRepo.findById(ticket.getTypeId())
                    .orElseThrow(() -> new NotFoundException("Ticket Type not found"));
            ticketToSave.setType(type);
            ticketToSave.setStatus(TicketStatus.PENDING);
            ticketToSave.setDescription(ticket.getDescription());
            ticketToSave.setUpdatedAt(LocalDateTime.now());

            Ticket updatedTicket = ticketsRepo.save(ticketToSave);
            // aggiorna ticket history
            updateTicketHistory(ticket, updatedTicket, user);

            return mapper.toTicketDTO(updatedTicket);

        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        } catch (ServiceNotFoundException e) {
            throw new ServiceNotFoundException(e.getMessage());
        }
    }

    public TicketDTO update(TicketLightInputDTO ticket, DatabaseUserDetails user)
            throws NotFoundException, AccessDeniedException {
        User currentUser = userService.getById(user.getId());

        Role employeeRole = roleRepo.findByName(RoleName.COMPANY_USER);
        Role customRole = roleRepo.findByName(RoleName.CLIENT);

        boolean errorFlag = false;

        Optional<Ticket> ticketEntity = ticketsRepo.findById(ticket.getId());

        if (ticketEntity.isEmpty()) {
            throw new NotFoundException("Ticket Not Found");
        }

        // verificare i permessi
        if (currentUser.getRoles().contains(employeeRole)) {
            errorFlag = currentUser.getAdminTickets().contains(ticketEntity.get()) ? false : true;

        } else if (currentUser.getRoles().contains(customRole)) {
            errorFlag = currentUser.getUserTickets().contains(ticketEntity.get()) ? false : true;
        }

        if (errorFlag == true) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        // esseguire update
        Ticket ticketToUpdate = ticketEntity.get();

        ticketToUpdate.setTitle(ticket.getTitle());
        ticketToUpdate.getAttachments().addAll(ticket.getAttachment());
        TicketType type = ticketTypeRepo.findById(ticket.getTypeId())
                .orElseThrow(() -> new NotFoundException("Ticket Type not found"));
        ticketToUpdate.setType(type);
        ticketToUpdate.setDescription(ticket.getDescription());
        ticketToUpdate.setUpdatedAt(LocalDateTime.now());

        Ticket updatedTicket = ticketsRepo.save(ticketToUpdate);
        TicketDTO ticketDTO = mapper.toTicketDTO(updatedTicket);
        // aggiorna ticket history
        updateTicketHistory(ticketDTO, updatedTicket, user);

        return mapper.toTicketDTO(updatedTicket);

    }

    @Transactional
    public void deleteById(Integer ticketId, DatabaseUserDetails user) throws NotFoundException, AccessDeniedException {
        Ticket ticketToDelete = ticketsRepo.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket Not Found"));

        User currentUser = userService.getById(user.getId());
        Role employeeRole = roleRepo.findByName(RoleName.COMPANY_USER);
        Role customerRole = roleRepo.findByName(RoleName.CLIENT);

        if (currentUser.getRoles().contains(employeeRole) || currentUser.getRoles().contains(customerRole)) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        if (ticketToDelete.getService() != null) {
            ticketToDelete.getService().getTickets().remove(ticketToDelete);
            ticketToDelete.setService(null);
        }

        if (ticketToDelete.getType() != null) {
            ticketToDelete.getType().getTickets().remove(ticketToDelete);
            ticketToDelete.setType(null);
        }

        ticketToDelete.setRequester(null);
        ticketToDelete.setAssignedTo(null);

        // elimina direttamente il ticket: JPA si occupa di cascade e orphan removal
        ticketsRepo.delete(ticketToDelete);
    }

    public void updateTicketHistory(TicketInputDTO ticketDTO, Ticket ticketEntity, DatabaseUserDetails currentUser)
            throws NotFoundException {
        TicketHistory ticketHistory = new TicketHistory();
        User currentUserEntity = userService.findById(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        LocalDateTime currentDate = LocalDateTime.now();

        ticketHistory.setChangedAt(currentDate);
        ticketHistory.setChangedBy(currentUserEntity);
        ticketHistory.setNotes(ticketDTO.getNotes());
        ticketHistory.setStatus(ticketDTO.getStatus());
        ticketHistory.setTicket(ticketEntity);

        ticketHistoryRepo.save(ticketHistory);

    }

    public void updateTicketHistory(TicketDTO ticketDTO, Ticket ticketEntity, DatabaseUserDetails currentUser)
            throws NotFoundException {
        TicketHistory ticketHistory = new TicketHistory();
        User currentUserEntity = userService.findById(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        LocalDateTime currentDate = LocalDateTime.now();

        ticketHistory.setChangedAt(currentDate);
        ticketHistory.setChangedBy(currentUserEntity);
        ticketHistory.setNotes("Admin changed the ticket basic infos");
        ticketHistory.setStatus(ticketDTO.getStatus());
        ticketHistory.setTicket(ticketEntity);

        ticketHistoryRepo.save(ticketHistory);

    }
}
