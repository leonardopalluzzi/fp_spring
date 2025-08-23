package org.finalproject.java.fp_spring.Repositories;

import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByCompany(Company company);

    List<User> findByServicesId(Integer serviceId);

    Optional<User> findByUserTicketsContaining(Ticket ticket);

    Optional<User> findByAdminTicketsContaining(Ticket ticket);

    @Query("""
            SELECT u FROM User u
            JOIN u.customerServices cs
            JOIN cs.company c
            WHERE c.id = :companyId
            """)
    Page<User> findCustomersByCompanyId(@Param("companyId") Integer companyId, Specification<User> spec,
            Pageable pagination);

    @Query("""
            SELECT u FROM User u
            JOIN u.services s
            JOIN s.company c
            WHERE c.id = :companyId
            """)
    Page<User> findEmployeesByCompanyId(@Param("companyId") Integer companyId, Specification<User> spec,
            Pageable pagination);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN u.customerServices cs
            JOIN cs.operators o
            WHERE o.id = :operatorId
            """)
    Page<User> findCustomerByOperatorId(@Param("operatorId") Integer operatorId, Specification<User> spec,
            Pageable pagination);

}
