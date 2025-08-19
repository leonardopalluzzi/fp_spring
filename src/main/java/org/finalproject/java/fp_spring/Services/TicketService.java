package org.finalproject.java.fp_spring.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<TicketDTO> findAllByUser(DatabaseUserDetails user) throws UsernameNotFoundException {

        Optional<User> foundUser = userService.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        Role customerRole = roleRepo.findByName(RoleName.CLIENT);
        List<Ticket> ticketsEntity = new ArrayList<>();
        if (foundUser.get().getRoles().contains(customerRole)) {
            ticketsEntity = foundUser.get().getUserTickets();
        } else {
            ticketsEntity = foundUser.get().getAdminTickets();
        }

        List<TicketDTO> ticketsDTO = new ArrayList<>();
        for (Ticket ticket : ticketsEntity) {
            TicketDTO ticketDTO = mapper.toTicketDTO(ticket);
            ticketsDTO.add(ticketDTO);
        }

        return ticketsDTO;

    }

}
