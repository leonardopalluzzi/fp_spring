package org.finalproject.java.fp_spring.DTOs;

import org.finalproject.java.fp_spring.Enum.RoleName;

public class RoleLightDTO {
    private Integer id;
    private RoleName name;

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
}
