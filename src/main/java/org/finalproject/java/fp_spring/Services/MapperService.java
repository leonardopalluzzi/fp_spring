package org.finalproject.java.fp_spring.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.finalproject.java.fp_spring.DTOs.CompanyDTO;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceDTO;
import org.finalproject.java.fp_spring.DTOs.CompanyServiceLightDTO;
import org.finalproject.java.fp_spring.DTOs.RoleDTO;
import org.finalproject.java.fp_spring.DTOs.RoleLightDTO;
import org.finalproject.java.fp_spring.DTOs.ServiceTypeDTO;
import org.finalproject.java.fp_spring.DTOs.TicketDTO;
import org.finalproject.java.fp_spring.DTOs.TicketLightDTO;
import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.DTOs.UserLightDTO;
import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.User;
import org.springframework.stereotype.Component;

@Component
public class MapperService {

    public CompanyDTO toCompanyDTO(Company entity) {
        if (entity == null)
            return null;

        CompanyDTO dto = new CompanyDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setPIva(entity.getPIva());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getServices() != null) {
            dto.setServices(entity.getServices().stream().map(this::toCompanyServiceLightDTO).toList());
        }

        if (entity.getUsers() != null) {
            dto.setUsers(entity.getUsers().stream().map(this::toUserLightDTO).toList());
        }

        return dto;
    }

    public CompanyServiceDTO toCompanyServiceDTO(CompanyService entity) {
        if (entity == null)
            return null;

        CompanyServiceDTO dto = new CompanyServiceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCode(entity.getCode());
        if (entity.getServiceType() != null) {
            ServiceTypeDTO stDto = new ServiceTypeDTO();
            stDto.setId(entity.getServiceType().getId());
            stDto.setName(entity.getServiceType().getName());
            dto.setServiceType(stDto);
        }
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getTickets() != null) {
            dto.setTickets(entity.getTickets().stream().map(this::toTicketLightDTO).toList());
        }
        dto.setTicketTypes(entity.getTicketTypes());
        if (entity.getOperators() != null) {
            dto.setOperators(entity.getOperators().stream().map(this::toUserLightDTO).toList());
        }
        if (entity.getCustomers() != null) {
            dto.setCustomers(entity.getCustomers().stream().map(this::toUserLightDTO).toList());
        }
        return dto;
    }

    public CompanyServiceLightDTO toCompanyServiceLightDTO(CompanyService entity) {
        if (entity == null)
            return null;

        CompanyServiceLightDTO dto = new CompanyServiceLightDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCode(entity.getCode());
        if (entity.getServiceType() != null) {
            ServiceTypeDTO stDto = new ServiceTypeDTO();
            stDto.setId(entity.getServiceType().getId());
            stDto.setName(entity.getServiceType().getName());
            dto.setServiceType(stDto);
        }
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public RoleDTO toRoleDTO(Role entity) {
        if (entity == null)
            return null;

        RoleDTO dto = new RoleDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if (entity.getUsers() != null) {
            dto.setUsers(entity.getUsers().stream().map(this::toUserLightDTO).toList());
        }
        return dto;
    }

    public RoleLightDTO toRoleLightDTO(Role entity) {
        if (entity == null)
            return null;

        RoleLightDTO dto = new RoleLightDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public TicketDTO toTicketDTO(Ticket entity) {
        if (entity == null)
            return null;

        TicketDTO dto = new TicketDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setAttachments(entity.getAttached());
        dto.setService(toCompanyServiceLightDTO(entity.getService()));
        dto.setRequester(toUserLightDTO(entity.getRequester()));
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public TicketLightDTO toTicketLightDTO(Ticket entity) {
        if (entity == null)
            return null;

        TicketLightDTO dto = new TicketLightDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public UserDTO toUserDTO(User entity) {
        if (entity == null)
            return null;

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getServices() != null) {
            List<CompanyServiceLightDTO> services = new ArrayList<>();
            for (CompanyService service : entity.getServices()) {
                services.add(toCompanyServiceLightDTO(service));
            }
            dto.setServices(services);
        }
        if (entity.getCustomerServices() != null) {
            List<CompanyServiceLightDTO> customerService = new ArrayList<>();
            for (CompanyService service : entity.getCustomerServices()) {
                customerService.add(toCompanyServiceLightDTO(service));
            }
            dto.setServices(customerService);
        }
        if (entity.getUserTickets() != null) {
            List<TicketLightDTO> userTickets = new ArrayList<>();
            for (Ticket userTicket : entity.getUserTickets()) {
                userTickets.add(toTicketLightDTO(userTicket));
            }
            dto.setUserTickets(userTickets);
        }
        if (entity.getAdminTickets() != null) {
            List<TicketLightDTO> adminTickets = new ArrayList<>();
            for (Ticket adminTicket : entity.getAdminTickets()) {
                adminTickets.add(toTicketLightDTO(adminTicket));
            }
            dto.setAdminTickets(adminTickets);
        }
        Set<RoleLightDTO> rDto = new HashSet<>();
        for (Role role : entity.getRoles()) {
            rDto.add(toRoleLightDTO(role));
        }
        dto.setRoles(rDto);

        return dto;
    }

    public UserLightDTO toUserLightDTO(User entity) {
        if (entity == null)
            return null;

        UserLightDTO dto = new UserLightDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setCreatedAt(entity.getCreatedAt());
        Set<RoleLightDTO> rDto = new HashSet<>();
        for (Role role : entity.getRoles()) {
            rDto.add(toRoleLightDTO(role));
        }
        dto.setRoles(rDto);

        return dto;
    }

}
