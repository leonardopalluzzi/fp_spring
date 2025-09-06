package org.finalproject.java.fp_spring.DTOs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.finalproject.java.fp_spring.Enum.ServiceStatus;

public class CompanyServiceUpdateDTO {
    private String name;
    private String description;

    private Integer serviceTypeId;
    private List<TicketTypeDTO> ticketTypes = new ArrayList<>();
    private ServiceStatus status;

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
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

    public Integer getServiceTypeId() {
        return this.serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public List<TicketTypeDTO> getTicketTypes() {
        return this.ticketTypes;
    }

    public void setTicketTypes(List<TicketTypeDTO> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

}
