package org.finalproject.java.fp_spring.Controllers;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
@RequestMapping("admin/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String index(Model model, @PathVariable("id") Integer id,
            @RequestParam(name = "isService", required = false) Boolean isService,
            @RequestParam(name = "isOperator", required = false) Boolean isOperator) {

        List<User> users = new ArrayList<>();

        if (isService == null) {
            isService = false;
        }

        if (isOperator == null) {
            isOperator = false;
        }

        if (isService && isOperator) {
            users = userService.getOperatorsByService(id);
            Integer companyId = userService.findCompanyIdByServiceId(id);
            model.addAttribute("serviceId", id);
            model.addAttribute("companyId", companyId);
            model.addAttribute("isService", isService);

            String returnToIfService = "/admin/users/" + id
                    + "?isService=true&isOperator=true&returnTo=/admin/company/";
            model.addAttribute("returnToIfService", returnToIfService);

        } else if (isService && !isOperator) {
            users = userService.getCustomersByService(id);
            Integer companyId = userService.findCompanyIdByServiceId(id);
            model.addAttribute("serviceId", id);
            model.addAttribute("companyId", companyId);
            model.addAttribute("isService", isService);

            String returnToIfService = "/admin/users/" + id
                    + "?isService=true&isOperator=false&returnTo=/admin/company/";
            model.addAttribute("returnToIfService", returnToIfService);
        } else {
            users = userService.findByCompany(id);
            model.addAttribute("companyId", id);
        }

        model.addAttribute("users", users);
        model.addAttribute("isService", isService);
        model.addAttribute("isOperator", isOperator);

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

        return "redirect:/admin/users/" + company.getId();
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String create(Model model, @RequestParam("companyId") Integer companyId) {

        User user = new User();

        model.addAttribute("user", user);
        model.addAttribute("companyId", companyId);

        return "users/create";
    }

    @PostMapping("/store")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String store(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model,
            @RequestParam("companyId") Integer companyId) {
        User updatedUser = userService.insertNewUser(user, companyId);
        return "redirect:/admin/users/" + updatedUser.getCompany().getId();
    }

}
