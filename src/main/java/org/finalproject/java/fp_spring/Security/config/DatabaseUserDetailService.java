package org.finalproject.java.fp_spring.Security.config;

import java.util.Optional;
import java.util.Set;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepo;

    @Override
    public DatabaseUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = repo.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }

        return new DatabaseUserDetails(user.get());
    }

    public User registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        Role customerRole = roleRepo.findByName(RoleName.CLIENT);
        Set<Role> roles = Set.of(customerRole);
        user.setPassword(encodedPassword);
        user.setRoles(roles);

        return repo.save(user);
    }

}
