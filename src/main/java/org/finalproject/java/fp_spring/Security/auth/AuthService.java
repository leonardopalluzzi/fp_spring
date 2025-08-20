package org.finalproject.java.fp_spring.Security.auth;

import java.util.Optional;

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

    public AuthService(DatabaseUserDetailService userDetailService){
        this.dbUserService = userDetailService;
    }

    @Autowired
    private JwtService jwtService;

    @Override
    public String login(User user) throws BadCredentialsException {

        Optional<User> foundUser = userRepo.findByUsername(user.getUsername());

        if(foundUser.isEmpty()){
            throw new BadCredentialsException("Invalid credential");
        }

        DatabaseUserDetails userDTO = new DatabaseUserDetails(user);

        String token = jwtService.generateToken(userDTO);

        return token;
    }

    @Override
    public void logout(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }

    @Override
    public String register(User user) {
       if(userRepo.existsByUsername(user.getUsername())){
        throw new BadCredentialsException("Username already registered");
       }

       DatabaseUserDetails userDTO = new DatabaseUserDetails(user);

       dbUserService.registerUser(user);
       String token = jwtService.generateToken(
            new org.springframework.security.core.userdetails.User(
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getAuthorities() // or a list of GrantedAuthority
            )
        );

        // Optionally, you can return the token or any other information
        // related to the registration process.
       return token;
    }

}
