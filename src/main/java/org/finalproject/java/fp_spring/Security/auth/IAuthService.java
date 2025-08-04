package org.finalproject.java.fp_spring.Security.auth;

import org.finalproject.java.fp_spring.Models.User;

public interface IAuthService {

    String generateJwt(User user);
    String login(User user);
    void logout(String token);
    String register(User user);

}
