package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public class TicketHistoryInputDTO {
    private Integer ticketId;

    @Size(min = 10, max = 500, message = "Notes must be between 10 and 500 characters")
    private String notes;

    private Integer changedById;
    private LocalDateTime changedAt;
    private String status;

    public TicketHistoryInputDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public Integer getChangedById() {
        return changedById;
    }

    public void setChangedById(Integer changedById) {
        this.changedById = changedById;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }
}
