package org.finalproject.java.fp_spring.Specifications;

import java.util.Set;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.Ticket;
import org.finalproject.java.fp_spring.Models.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class UserSpecifications {

    public static Specification<User> usernameContains(String username) {
        if (username == null || username.isBlank()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.like(root.get("username"), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> emailContains(String email) {
        if (email == null || email.isBlank()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.like(root.get("email"), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> roleContains(String role) {
        if (role == null || role.isEmpty()) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> {
            Join<User, Role> roles = root.join("roles");
            return cb.equal(roles.get("name"), role);
        };
    }

    public static Specification<User> hasService(Integer serviceId) {
        if (serviceId == null) {
            return Specification.unrestricted();
        }
        return (root, query, cb) -> {
            Join<User, CompanyService> services = root.join("services");
            return cb.equal(services.get("id"), serviceId);
        };
    }

}
