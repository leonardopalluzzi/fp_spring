package org.finalproject.java.fp_spring.Specifications;

import java.time.LocalDateTime;

import org.finalproject.java.fp_spring.Models.Company;
import org.springframework.data.jpa.domain.Specification;

public class CompanySpecifications {

    public static Specification<Company> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Company> emailContains(String email) {
        if (email == null || email.isBlank()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");

    }

    public static Specification<Company> phoneContains(String phone) {

        if (phone == null || phone.isBlank()) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> cb.like(root.get("phone"), "%" + phone + "%");
    }

    public static Specification<Company> createdAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> {
            if (startDate == null)
                return cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
            if (endDate == null)
                return cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            return cb.between(root.get("createdAt"), startDate, endDate);
        };
    }
}
