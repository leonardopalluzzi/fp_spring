package org.finalproject.java.fp_spring.Specifications;

import java.time.LocalDateTime;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

import org.finalproject.java.fp_spring.Models.User;

public class ServiceSpecifications {

    public static Specification<CompanyService> nameContains(String name) {
        if (name == null || name.isEmpty()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.like(root.get("name"), name);
    }

    public static Specification<CompanyService> serviceDescriptionContains(String description) {
        if (description == null || description.isEmpty()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.like(root.get("description"), description);
    }

    public static Specification<CompanyService> hasServiceStatus(String status) {
        if (status == null || status.isEmpty()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<CompanyService> ServiceCreatedAfter(LocalDateTime createdAt) {
        if (createdAt == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), createdAt);
    }

    public static Specification<CompanyService> hasServiceType(String serviceType) {
        if (serviceType == null || serviceType.isEmpty()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.get("serviceType"), serviceType);
    }

    public static Specification<CompanyService> codeContains(String code) {
        if (code == null || code.isEmpty()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.like(root.get("code"), code);
    }

    public static Specification<CompanyService> belongsToEmployee(Integer userId) {
        return (root, query, cb) -> {
            Join<CompanyService, User> userJoin = root.join("user");
            return cb.and(
                    cb.equal(userJoin.get("id"), userId),
                    cb.isTrue(userJoin.get("isEmployee")));
        };
    }

    public static Specification<CompanyService> belongsToCompany(Integer companyId) {
        if (companyId == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.get("company").get("id"), companyId);
    }

    public static Specification<CompanyService> belongsToCustomer(Integer customerId) {
        if (customerId == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.join("customers").get("id"), customerId);
    }
}
