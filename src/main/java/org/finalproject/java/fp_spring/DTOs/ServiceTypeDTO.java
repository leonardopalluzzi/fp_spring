package org.finalproject.java.fp_spring.DTOs;

import java.util.List;

public class ServiceTypeDTO {
    private Integer id;
    private String name;
    private List<CompanyServiceLightDTO> services;

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

    public List<CompanyServiceLightDTO> getServices() {
        return this.services;
    }

    public void setServices(List<CompanyServiceLightDTO> services) {
        this.services = services;
    }

}
