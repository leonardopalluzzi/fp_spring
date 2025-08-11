package org.finalproject.java.fp_spring.Repositories;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(RoleName name);

}
