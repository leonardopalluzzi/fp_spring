package org.finalproject.java.fp_spring.Repositories;

import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<CompanyService, Integer> {

    boolean existsByCode(String code);

    List<CompanyService> findByCustomers_id(Integer customerId);

    List<CompanyService> findByOperators_id(Integer operatorId);

    Optional<CompanyService> findByTicketsContaining(Ticket ticket);
}
