package org.finalproject.java.fp_spring.DTOs;

import java.util.List;

import org.finalproject.java.fp_spring.Enum.RoleName;

public class RoleDTO {
    private Integer id;
    private RoleName name;

    private List<UserLightDTO> users;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleName getName() {
        return this.name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public List<UserLightDTO> getUsers() {
        return this.users;
    }

    public void setUsers(List<UserLightDTO> users) {
        this.users = users;
    }

}
