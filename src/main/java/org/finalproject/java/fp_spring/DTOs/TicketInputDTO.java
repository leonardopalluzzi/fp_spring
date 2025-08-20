package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.Attachment;
import org.finalproject.java.fp_spring.Models.TicketType;

public class TicketInputDTO {
    private Integer id; // non modificabile
    private String title;
    private List<Attachment> attachments;
    private UserLightDTO requester; // non modificabile
    private String description;

    private Integer typeId;
    private TicketStatus status;
    private LocalDateTime createdAt; // non modificabile

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
