package org.finalproject.java.fp_spring.RestControllers;

import java.nio.file.AccessDeniedException;

import javax.management.ServiceNotFoundException;

import org.apache.coyote.BadRequestException;
import org.finalproject.java.fp_spring.Exceptions.NotFoundException;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Services.ServiceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/relations/services")
public class ServiceManagementController {

    @Autowired
    ServiceManagementService serviceManagService;

    @PostMapping("/{serviceId}/operator/{userId}")
    public ResponseEntity<?> assingOperatorToService(@PathVariable("serviceId") Integer serviceId,
            @PathVariable("userId") Integer userId) {
        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            serviceManagService.assignOperatorToService(serviceId, userId, currentUser);
            return ResponseEntity.ok("Operator Assigned Correctly");

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("{serviceId}/operator/{userId}")
    public ResponseEntity<?> detachOperatorFromService(@PathVariable("serviceId") Integer serviceId,
            @PathVariable("userId") Integer userId) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            serviceManagService.detachOperatorFromService(serviceId, userId, currentUser);
            return ResponseEntity.ok("Operator Detached Correctly");

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @DeleteMapping("/{serviceId}/customer/{userId}")
    public ResponseEntity<?> detachCustomerFromService(@PathVariable("serviceId") Integer serviceId,
            @PathVariable("userId") Integer userId) {

        DatabaseUserDetails currentUser = (DatabaseUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        try {
            serviceManagService.detachCustomerFromService(serviceId, userId, currentUser);
            return ResponseEntity.ok("Customer Detached Correctly");

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
