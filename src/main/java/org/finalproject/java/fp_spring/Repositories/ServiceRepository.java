package org.finalproject.java.fp_spring.Repositories;

import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceRepository extends JpaRepository<CompanyService, Integer> {

    boolean existsByCode(String code);

    List<CompanyService> findByCustomers_id(Integer customerId);

    List<CompanyService> findByOperators_id(Integer operatorId);

    Optional<CompanyService> findByTicketsContaining(Ticket ticket);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM services s WHERE s.id = :serviceId", nativeQuery = true)
    int deleteCustom(@Param("serviceId") Integer serviceId);
}
