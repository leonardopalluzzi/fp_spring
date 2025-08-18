package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompanyDTO {
    private Integer id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String pIva;
    private LocalDateTime createdAt;

    private List<CompanyServiceLightDTO> services = new ArrayList<>();
    private List<UserLightDTO> users = new ArrayList<>();

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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPIva() {
        return this.pIva;
    }

    public void setPIva(String pIva) {
        this.pIva = pIva;
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

    public List<UserLightDTO> getUsers() {
        return this.users;
    }

    public void setUsers(List<UserLightDTO> users) {
        this.users = users;
    }

}
