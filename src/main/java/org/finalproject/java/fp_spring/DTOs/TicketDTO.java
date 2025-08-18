package org.finalproject.java.fp_spring.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.Attachment;
import org.finalproject.java.fp_spring.Models.TicketType;

public class TicketDTO {
    private Integer id;
    private String title;
    private List<Attachment> attachments;
    private CompanyServiceLightDTO service;
    private UserLightDTO requester;

    private TicketType type;
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

    public TicketType getType() {
        return this.type;
    }

    public void setType(TicketType type) {
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
