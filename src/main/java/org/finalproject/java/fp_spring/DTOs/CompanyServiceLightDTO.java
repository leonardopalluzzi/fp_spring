package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;

import org.finalproject.java.fp_spring.Enum.ServiceStatus;
import org.finalproject.java.fp_spring.Models.ServiceType;

public class CompanyServiceLightDTO {
    private Integer id;
    private String name;
    private String description;
    private String code;

    private ServiceTypeDTO serviceType;
    private ServiceStatus status = ServiceStatus.INACTIVE;
    private LocalDateTime createdAt;

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

}
