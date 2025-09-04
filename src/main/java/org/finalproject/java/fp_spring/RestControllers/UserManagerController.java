package org.finalproject.java.fp_spring.RestControllers;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.DTOs.UserLightDTO;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtException;

@RestController
@RequestMapping("api/v1/users/manage")
public class UserManagerController {

    @Autowired
    UserService userService;

    @GetMapping("/byService/{id}")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN', 'COMPANY_USER')")
    public ResponseEntity<?> getEmployeeByService(
            @PathVariable("id") Integer serviceId,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "page", required = true) Integer page) {

        try {

            Page<UserLightDTO> employees = userService.getOperatorsByServiceDTO(serviceId, username, email, page);
            return ResponseEntity.ok(Map.of("state", "success", "result", employees));

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "expired", "message", e.getMessage()));
        }

    }

    @GetMapping("/byCompany/{companyId}/service/{serviceId}")
    @PreAuthorize("hasAnyAuthority('COMPANY_ADMIN')")
    public ResponseEntity<?> getEmployeesByCompanyId(
            @PathVariable("companyId") Integer companyId,
            @PathVariable("serviceId") Integer serviceId,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "page", required = true) Integer page,
            @RequestParam(name = "exclude", required = true) boolean exclude) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            Page<UserLightDTO> employeesDTO = userService.getOperatorsByCompanyId(companyId, currentUser, page,
                    username, email, serviceId, exclude);

            return ResponseEntity.ok(Map.of("state", "success", "result", employeesDTO));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "error", "message", e.getMessage()));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "expired", "message", e.getMessage()));

        }
    }
}