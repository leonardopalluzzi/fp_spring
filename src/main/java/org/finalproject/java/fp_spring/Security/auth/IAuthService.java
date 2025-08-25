package org.finalproject.java.fp_spring.Security.auth;

import org.finalproject.java.fp_spring.DTOs.UserInputDTO;
import org.finalproject.java.fp_spring.Models.User;

public interface IAuthService {

    String login(User user);

    String register(UserInputDTO user);

}
