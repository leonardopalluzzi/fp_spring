package org.finalproject.java.fp_spring.Services;

import java.util.Optional;

import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.finalproject.java.fp_spring.Services.Interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepo;

    @Override
    public void deleteById(Integer userId) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findById(userId);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found");
        }

        userRepo.deleteById(userId);
    }

    public User getById(Integer id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }

}
