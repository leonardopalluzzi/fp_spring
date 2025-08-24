package org.finalproject.java.fp_spring.Repositories;

import java.util.List;

import org.finalproject.java.fp_spring.Models.TicketHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistroyRepository extends JpaRepository<TicketHistory, Integer> {

    Page<TicketHistory> findAllByTicketId(Integer ticketId, Pageable pagination);

}