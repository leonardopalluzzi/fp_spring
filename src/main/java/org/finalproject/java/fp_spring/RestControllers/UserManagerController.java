package org.finalproject.java.fp_spring.RestControllers;

import java.util.List;
import java.util.Map;

import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtException;

@RestController
@RequestMapping("api/v1/users/manage")
public class UserManagerController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN', 'COMPANY_USER')")
    public ResponseEntity<?> getEmployeeByService(@PathVariable("id") Integer serviceId) {

        try {

            List<User> employees = userService.getOperatorsByService(serviceId);
            return ResponseEntity.ok(employees);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "expired", "message", e.getMessage()));
        }

    }

}