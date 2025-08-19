package org.finalproject.java.fp_spring.Repositories;

import org.finalproject.java.fp_spring.Models.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {

}