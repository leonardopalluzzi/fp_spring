package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.Attachment;

public class TicketDTO {
    private Integer id;
    private String title;
    private List<Attachment> attachments;
    private CompanyServiceLightDTO service;
    private UserLightDTO requester;

    private TicketTypeDTO type;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private UserLightDTO assignedTo;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String desctiption) {
        this.description = desctiption;
    }

    public UserLightDTO getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserLightDTO assignedTo) {
        this.assignedTo = assignedTo;
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

    public CompanyServiceLightDTO getService() {
        return this.service;
    }

    public void setService(CompanyServiceLightDTO service) {
        this.service = service;
    }

    public UserLightDTO getRequester() {
        return this.requester;
    }

    public void setRequester(UserLightDTO requester) {
        this.requester = requester;
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
