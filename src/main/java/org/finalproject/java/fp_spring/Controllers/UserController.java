package org.finalproject.java.fp_spring.Controllers;

import java.util.List;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Models.User;
import org.finalproject.java.fp_spring.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("admin/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthrity")
    public String index(Model model, @PathVariable("id") Integer id) {

        List<User> users = userService.findByCompany(id);
        model.addAttribute("users", users);

        return "users/index";

    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String edit(@PathVariable("id") Integer id, Model model) {

        User user = userService.getById(id);
        model.addAttribute("user", user);

        return "users/edit";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String update(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
        }

        User userToUpdate = userService.getById(user.getId());
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(user.getPassword());

        userService.save(userToUpdate);

        Company company = userToUpdate.getCompany();
        Integer companyId = company.getId();

        return "redirect:/admin/users/" + companyId;

    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String delete(@PathVariable("id") Integer id) {
        User user = userService.getById(id);
        Company company = user.getCompany();
        userService.deleteById(id);

        return "redirect:/admin/company/edit/" + company.getId();
    }

}
