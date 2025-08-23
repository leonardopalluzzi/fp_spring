package org.finalproject.java.fp_spring.Specifications;

import org.finalproject.java.fp_spring.Models.User;
import org.springframework.data.jpa.domain.Specification;

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

    public static getCopmanyRelatedUsers(Company company){
        if(company == null){
            return Specification.unrestricted();
        }
        return (root, query, cb) -> cb.findAll(root.getCompany());
    }

}
