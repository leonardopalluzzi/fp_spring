package org.finalproject.java.fp_spring.Specifications;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Models.Role;
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

    public static Specification<User> hasCustomerInCompany(Integer companyId) {
        return (root, query, cb) -> {
            Join<Object, Object> cs = root.join("customerServices");
            Join<Object, Object> c = cs.join("company");
            return cb.equal(c.get("id"), companyId);
        };
    }

    public static Specification<User> hasEmployeeInCompany(Integer companyId) {
        return (root, query, cb) -> {
            Join<Object, Object> s = root.join("services");
            Join<Object, Object> c = s.join("company");
            return cb.equal(c.get("id"), companyId);
        };
    }

    public static Specification<User> hasCustomerByOperator(Integer operatorId) {
        return (root, query, cb) -> {
            Join<Object, Object> cs = root.join("customerServices");
            Join<Object, Object> o = cs.join("operators");
            return cb.equal(o.get("id"), operatorId);
        };
    }

    public static Specification<User> hasOperator(Integer operatorId) {
        return (root, query, cb) -> {
            root.join("customerServices").join("operators");
            return cb.equal(root.join("customerServices").join("operators").get("id"), operatorId);
        };
    }

}
