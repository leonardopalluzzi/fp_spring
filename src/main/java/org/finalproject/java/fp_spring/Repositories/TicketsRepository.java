package org.finalproject.java.fp_spring.Repositories;

import java.util.List;

import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface TicketsRepository extends JpaRepository<Ticket, Integer>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findAllByRequester(User user);

    List<Ticket> findAllByAssignedTo(User user);

    List<Ticket> findByService_Company_Id(Integer companyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Ticket t WHERE t.requester = :user AND t.service.id = :serviceId")
    void deleteByRequesterAndServiceId(@Param("user") User user, @Param("serviceId") Integer serviceId);

    List<Ticket> findByAssignedTo(User assignedTo);
}
