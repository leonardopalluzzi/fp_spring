package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserLightDTO {

    private Integer id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    private Set<RoleLightDTO> roles = new HashSet<>();

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

    public Set<RoleLightDTO> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<RoleLightDTO> roles) {
        this.roles = roles;
    }

}
