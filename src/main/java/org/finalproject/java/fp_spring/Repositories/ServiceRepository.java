package org.finalproject.java.fp_spring.Repositories;

import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceRepository
        extends JpaRepository<CompanyService, Integer>, JpaSpecificationExecutor<CompanyService> {

    boolean existsByCode(String code);

    Page<CompanyService> findAllByCompanyId(Specification<CompanyService> spec, Pageable pagination);

    List<CompanyService> findAllByCompanyId(Integer companyId);

    Page<CompanyService> findByCustomers_id(Specification<CompanyService> spec,
            Pageable pagination);

    Page<CompanyService> findByOperators_id(Specification<CompanyService> spec,
            Pageable pagination);

    Optional<CompanyService> findByTicketsContaining(Ticket ticket);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM services s WHERE s.id = :serviceId", nativeQuery = true)
    int deleteCustom(@Param("serviceId") Integer serviceId);
}
