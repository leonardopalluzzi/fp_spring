package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.finalproject.java.fp_spring.Models.Role;

public class UserDTO {

    private Integer id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    private List<CompanyServiceLightDTO> services;
    private List<CompanyServiceLightDTO> customerService;
    private List<TicketLightDTO> userTickets;
    private List<TicketLightDTO> adminTickets;
    private Set<Role> roles = new HashSet<>();

    public List<TicketLightDTO> getAdminTickets() {
        return adminTickets;
    }

    public void setAdminTickets(List<TicketLightDTO> adminTickets) {
        this.adminTickets = adminTickets;
    }

    public List<CompanyServiceLightDTO> getCustomerService() {
        return customerService;
    }

    public void setCustomerService(List<CompanyServiceLightDTO> customerService) {
        this.customerService = customerService;
    }

    public List<TicketLightDTO> getUserTickets() {
        return userTickets;
    }

    public void setUserTickets(List<TicketLightDTO> userTickets) {
        this.userTickets = userTickets;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CompanyServiceLightDTO> getServices() {
        return this.services;
    }

    public void setServices(List<CompanyServiceLightDTO> services) {
        this.services = services;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
