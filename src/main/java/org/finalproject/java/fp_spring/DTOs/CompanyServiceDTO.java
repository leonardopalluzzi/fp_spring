package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.Enum.ServiceStatus;

public class CompanyServiceDTO {
    private Integer id;
    private String name;
    private String description;
    private String code;

    private ServiceTypeDTO serviceType;
    private ServiceStatus status = ServiceStatus.INACTIVE;
    private LocalDateTime createdAt;
    private List<TicketLightDTO> tickets = new ArrayList<>();
    private List<TicketTypeDTO> ticketTypes = new ArrayList<>();
    private List<UserLightDTO> customers;
    private List<UserLightDTO> operators;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ServiceTypeDTO getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(ServiceTypeDTO serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceStatus getStatus() {
        return this.status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TicketLightDTO> getTickets() {
        return this.tickets;
    }

    public void setTickets(List<TicketLightDTO> tickets) {
        this.tickets = tickets;
    }

    public List<TicketTypeDTO> getTicketTypes() {
        return this.ticketTypes;
    }

    public void setTicketTypes(List<TicketTypeDTO> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

    public List<UserLightDTO> getCustomers() {
        return customers;
    }

    public void setCustomers(List<UserLightDTO> customers) {
        this.customers = customers;
    }

    public List<UserLightDTO> getOperators() {
        return operators;
    }

    public void setOperators(List<UserLightDTO> operators) {
        this.operators = operators;
    }

}
