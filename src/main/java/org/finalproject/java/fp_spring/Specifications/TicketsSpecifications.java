package org.finalproject.java.fp_spring.Specifications;

import java.time.LocalDateTime;

import org.finalproject.java.fp_spring.Enum.TicketStatus;
import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.TicketType;
import org.finalproject.java.fp_spring.Models.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class TicketsSpecifications {

    public static Specification<Ticket> hasType(TicketType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Ticket> hasStatus(TicketStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Ticket> titleContains(String title) {
        return (root, query, cb) -> title == null ? null
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Ticket> descriptionContains(String description) {
        return (root, query, cb) -> description == null ? null
                : cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    public static Specification<Ticket> createdAfter(LocalDateTime createdAt) {
        return (root, query, cb) -> createdAt == null ? null
                : cb.greaterThanOrEqualTo(root.get("createdAt"), createdAt);
    }

    public static Specification<Ticket> belongsToRequester(User requester) {
        return (root, query, cb) -> requester == null ? null : cb.equal(root.get("requester"), requester);
    }

    public static Specification<Ticket> belongsToAssignee(User assignee) {
        return (root, query, cb) -> assignee == null ? null : cb.equal(root.get("assignedTo"), assignee);
    }

    public static Specification<Ticket> belongsToCompany(Integer companyId) {
        return (root, query, cb) -> companyId == null ? null
                : cb.equal(root.get("service").get("company").get("id"), companyId);
    }

    public static Specification<Ticket> belongsToService(Integer serviceId) {
        if (serviceId == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.get("service").get("id"), serviceId);
    }

    public static Specification<Ticket> belongsToOperatorService(Integer operatorId) {
        if (operatorId == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> {
            Join<Ticket, CompanyService> serviceJoin = root.join("service");
            Join<CompanyService, User> operatorJoin = serviceJoin.join("operators");
            return cb.equal(operatorJoin.get("id"), operatorId);
        };
    }

    public static Specification<Ticket> hasNoAssignee() {
        return (root, query, cb) -> cb.isNull(root.get("assignedTo"));
    }

}
