package org.finalproject.java.fp_spring.DTOs;

import java.util.List;

import org.finalproject.java.fp_spring.Models.Attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TicketLightInputDTO {
    private Integer id;
    private List<Attachment> attachment;

    @NotBlank
    @Size(min = 5, max = 150, message = "Title must be between 5 and 150 characters")
    private String title;

    @Size(min = 10, max = 1200, message = "Description must be between 10 and 1200 characters")
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
