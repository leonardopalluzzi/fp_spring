package org.finalproject.java.fp_spring.RestControllers;

import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Security.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    // login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        
        try {
        String token = authService.login(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
            
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Bad Credentials");
        }

    }

    // register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String token) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // refershtoken

}
