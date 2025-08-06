package org.finalproject.java.fp_spring.Controllers;

import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Services.AdminSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminSerivce adminSerivce;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String index(Model model) {

        List<Company> copmanies = adminSerivce.GetAllPaginated();
        model.addAttribute("copmanies", copmanies);

        return "admin/index";
    }

}
