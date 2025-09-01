package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;

import org.finalproject.java.fp_spring.Enum.ServiceStatus;
import org.finalproject.java.fp_spring.Enum.TicketStatus;

public class TicketHistoryInputDTO {
    private Integer ticketId;
    private String notes;
    private Integer changedById;
    private LocalDateTime changedAt;
    private TicketStatus status;

    public TicketHistoryInputDTO(Integer ticketId, String notes, Integer changedById, LocalDateTime changedAt,TicketStatus status){
        this.ticketId = ticketId;
        this.notes = notes;
        this.changedById = changedById;
        this.changedAt = changedAt;
        this.status = status;
    }


    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
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
