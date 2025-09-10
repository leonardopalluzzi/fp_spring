package org.finalproject.java.fp_spring.DTOs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.finalproject.java.fp_spring.Enum.ServiceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyServiceInputDTO {

    @NotBlank
    @Size(min = 5, max = 200, message = "Name must be between 5 and 200 characters")
    private String name;

    @Size(min = 10, max = 1200, message = "Description must be between 10 and 1200 characters")
    private String description;

    private Integer serviceTypeId;
    private List<Map<String, String>> ticketTypes = new ArrayList<>();
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

    public List<Map<String, String>> getTicketTypes() {
        return this.ticketTypes;
    }

    public void setTicketTypes(List<Map<String, String>> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

}
