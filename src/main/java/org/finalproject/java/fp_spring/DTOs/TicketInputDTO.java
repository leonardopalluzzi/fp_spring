package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.Attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TicketInputDTO {
    private Integer id; // non modificabile

    @NotBlank
    @Size(min = 5, max = 150, message = "Title must be between 5 and 150 characters")
    private String title;
    private List<Attachment> attachments = new ArrayList<>();
    private UserLightDTO requester; // non modificabile

    @Size(min = 10, max = 1200, message = "Description must be between 10 and 1200 characters")
    private String description;

    private Integer typeId;
    private TicketStatus status;
    private LocalDateTime createdAt; // non modificabile
    private Integer assignedToId;

    @Size(min = 10, max = 500, message = "Notes must be between 10 and 500 characters")
    private String notes; // per storico

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Integer assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public UserLightDTO getRequester() {
        return this.requester;
    }

    public void setRequester(UserLightDTO requester) {
        this.requester = requester;
    }

    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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
