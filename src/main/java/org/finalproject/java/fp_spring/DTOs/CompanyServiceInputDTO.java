package org.finalproject.java.fp_spring.DTOs;

import java.util.ArrayList;
import java.util.List;

public class CompanyServiceInputDTO {
    private String name;
    private String description;

    private Integer serviceTypeId;
    private List<Integer> ticketTypesIds = new ArrayList<>();

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

    public List<Integer> getTicketTypeIds() {
        return this.ticketTypesIds;
    }

    public void setTicketTypeIds(List<Integer> ticketTypesIds) {
        this.ticketTypesIds = ticketTypesIds;
    }

}
