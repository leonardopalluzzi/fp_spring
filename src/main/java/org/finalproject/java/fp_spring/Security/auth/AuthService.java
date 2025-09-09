package org.finalproject.java.fp_spring.Security.auth;

import java.util.Optional;

import org.finalproject.java.fp_spring.DTOs.UserDTO;
import org.finalproject.java.fp_spring.DTOs.UserInputDTO;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetailService;
import org.finalproject.java.fp_spring.Security.config.DatabaseUserDetails;
import org.finalproject.java.fp_spring.Security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepository userRepo;

    private DatabaseUserDetailService dbUserService;

    public AuthService(DatabaseUserDetailService userDetailService) {
        this.dbUserService = userDetailService;
    }

    @Autowired
    private JwtService jwtService;

    @Override
    public String login(User user) throws BadCredentialsException {

        Optional<User> foundUser = userRepo.findByUsername(user.getUsername());

        if (foundUser.isEmpty()) {
            throw new BadCredentialsException("Invalid credential");
        }

        DatabaseUserDetails userDTO = new DatabaseUserDetails(foundUser.get());

        String token = jwtService.generateToken(userDTO);

        return token;
    }

    // ad uso esclusivo dei clienti
    @Override
    public String register(UserInputDTO userDTO) throws BadCredentialsException {
        if (userRepo.existsByUsername(userDTO.getUsername())) {
            throw new BadCredentialsException("Username already registered");
        }

        User userEntity = new User(userDTO);
        User savedUserEntity = dbUserService.registerUser(userEntity);
        DatabaseUserDetails savedUser = new DatabaseUserDetails(savedUserEntity);
        String token = jwtService.generateToken(savedUser);

        return token;
    }

}
