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

    @Query(value = """
            SELECT u.* FROM users u
            JOIN customer_service cs ON cs.user_id = u.id
            JOIN operator_service os ON os.user_id = u.id
            JOIN services s ON os.service_id = s.id
            JOIN companies c ON s.company_id = c.id
            WHERE c.id = :companyId
            """, countQuery = """
            SELECT count(*) FROM users u
            JOIN customer_service cs ON cs.user_id = u.id
            JOIN operator_service os ON os.user_id = u.id
            JOIN services s ON os.service_id = s.id
            JOIN companies c ON s.company_id = c.id
            WHERE c.id = :companyId
            """, nativeQuery = true)
    Page<User> findAllForAdminPaged(Pageable pagination, Specification<User> spec,
            @Param("companyId") Integer companyId);

}
