package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;

import org.finalproject.java.fp_spring.Enum.TicketStatus;

public class TicketLightDTO {
    private Integer id;
    private String title;
    private TicketTypeDTO type;
    private TicketStatus status;
    private LocalDateTime createdAt;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TicketTypeDTO getType() {
        return this.type;
    }

    public void setType(TicketTypeDTO type) {
        this.type = type;
    }

    public TicketStatus getStatus() {
        return this.status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
