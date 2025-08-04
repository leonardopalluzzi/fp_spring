package org.finalproject.java.fp_spring.Security.auth;

import java.util.Optional;

import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

public class AuthService implements IAuthService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public String generateJwt(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateJwt'");
    }

    @Override
    public String login(User user) {

        Optional<User> foundUser = userRepo.findByUsername(user.getUsername());

        if(foundUser.isEmpty()){
            throw new BadCredentialsException("Invalid credential");
        }

        String token = generateJwt(foundUser.get());

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

       userRepo.save(user);
       String token = generateJwt(user);
       return token;
    }

}
