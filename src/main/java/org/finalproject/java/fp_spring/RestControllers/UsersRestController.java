package org.finalproject.java.fp_spring.RestControllers;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.finalproject.java.fp_spring.DTOs.UserAdminIndexDTO;
import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersRestController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> index(@RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "page", required = false) int page) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.COMPANY_ADMIN.toString()))) {
            // meotodo per lista user admin con dto
            UserAdminIndexDTO allUsers = userService.getAllForAdminFiltered(currentUser, username, email, page);

            return ResponseEntity.ok(allUsers);

        } else if (currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.COMPANY_USER.toString()))) {
            // dto per lista users impiegato
            Page<UserDTO> customers = userService.getAllForEmployeeFiltered(currentUser, username, email, page);

            return ResponseEntity.ok(customers);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
