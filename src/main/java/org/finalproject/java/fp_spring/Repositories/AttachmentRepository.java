package org.finalproject.java.fp_spring.Repositories;

import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Attachment;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Optional<Attachment> findByTicket(Ticket ticket);
}