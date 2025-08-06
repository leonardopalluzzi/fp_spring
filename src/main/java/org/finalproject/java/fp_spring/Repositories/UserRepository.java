package org.finalproject.java.fp_spring.Repositories;

import java.util.Optional;

import org.finalproject.java.fp_spring.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}
