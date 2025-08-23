package org.finalproject.java.fp_spring.RestControllers;

import java.nio.file.AccessDeniedException;

import org.finalproject.java.fp_spring.DTOs.UserAdminIndexDTO;
import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.DTOs.UserInputDTO;
import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

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

        // controllo per far si che i customer non abbiano mai accesso ai dati,
        // disattivato per testing
        // if (currentUser.getAuthorities().contains(new
        // SimpleGrantedAuthority(RoleName.CLIENT.toString()))) {
        // return ResponseEntity.badRequest().build();
        // }

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to access this resource");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable("id") Integer id) {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            UserDTO user = userService.getByIdRoleWise(currentUser, id);
            return ResponseEntity.ok(user);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }

    }

    @PutMapping("/store/{id}")
    @PreAuthorize("hasAuthority(RoleName.COMPANY_ADMIN.ToString())")
    public ResponseEntity<?> store(@Valid @RequestBody UserInputDTO user, BindingResult bindingResult,
            @PathVariable("id") Integer serviceId) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("There are errors in some fields");
        }

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

    }

}
