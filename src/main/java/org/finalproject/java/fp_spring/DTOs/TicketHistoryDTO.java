package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;

public class TicketHistoryDTO {
    private Integer id;
    private TicketDTO ticket;
    private String notes;
    private UserDTO changedBy;
    private LocalDateTime changedAt;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TicketDTO getTicket() {
        return this.ticket;
    }

    public void setTicket(TicketDTO ticket) {
        this.ticket = ticket;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UserDTO getChangedBy() {
        return this.changedBy;
    }

    public void setChangedBy(UserDTO changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return this.changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

}
