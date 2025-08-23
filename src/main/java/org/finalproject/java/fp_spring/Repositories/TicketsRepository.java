package org.finalproject.java.fp_spring.Repositories;

import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketsRepository extends JpaRepository<Ticket, Integer>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findAllByRequester(User user);

    List<Ticket> findAllByAssignedTo(User user);

    List<Ticket> findByService_Company_Id(Integer companyId);
}
