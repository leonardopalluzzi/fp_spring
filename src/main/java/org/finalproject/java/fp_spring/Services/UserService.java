package org.finalproject.java.fp_spring.Services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.naming.NameNotFoundException;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.Role;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Repositories.CompanyRepository;
import org.finalproject.java.fp_spring.Repositories.RoleRepository;
import org.finalproject.java.fp_spring.Repositories.UserRepository;
import org.finalproject.java.fp_spring.Services.Interfaces.IUserService;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    CompanyRepository companyRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void deleteById(Integer userId) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findById(userId);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found");
        }

        userRepo.deleteById(userId);
    }

    public List<User> findByCompany(Integer companyId) {
        Optional<Company> company = companyRepo.findById(companyId);
        List<User> users = userRepo.findByCompany(company.get());
        return users;
    }

    public User getById(Integer id) {
        Optional<User> user = userRepo.findById(id);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user.get();
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void save(User user) {
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepo.save(user);
    }

    public User insertNewUser(User user, Integer companyId){
        User userWithCompany = setCompanyById(user, companyId);
        User updatedUser = setAdminRole(userWithCompany);
        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        userRepo.save(updatedUser);

        return updatedUser;
    }

    public User setCompanyById(User user, Integer companyId){
        Optional<Company> company = companyRepo.findById(companyId);
        user.setCompany(company.get());
        
        return user;
    }

    public User setAdminRole(User user){
        Role role = roleRepo.findByName(RoleName.COMPANY_ADMIN);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);

        return user;
    }
}
