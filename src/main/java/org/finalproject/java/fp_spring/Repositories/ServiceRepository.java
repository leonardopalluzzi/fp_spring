package org.finalproject.java.fp_spring.Repositories;

import org.finalproject.java.fp_spring.Models.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Integer> {

    boolean existsByCode(String code);
}