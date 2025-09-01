package org.finalproject.java.fp_spring.DTOs;

import java.util.List;

import org.finalproject.java.fp_spring.Models.Attachment;

public class TicketLightInputDTO {
    private Integer id;
    private List<Attachment> attachment;
    private String title;
    private String description;
    private Integer typeId;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Attachment> getAttachment() {
        return this.attachment;
    }

    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

}
