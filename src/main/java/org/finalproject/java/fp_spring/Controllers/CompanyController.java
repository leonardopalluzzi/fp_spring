package org.finalproject.java.fp_spring.Controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String index(Model model, @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate) {

        if (name != null) {
            List<Company> copmanies = companyService.GetAllPaginatedByName(name);
            model.addAttribute("copmanies", copmanies);
        } else {
            List<Company> copmanies = companyService.GetAllPaginated();
            model.addAttribute("copmanies", copmanies);
        }

        if (email != null || phone != null || startDate != null || endDate != null) {
            List<Company> companies = companyService.GetAllFiltered(email, phone, startDate, endDate);
            model.addAttribute("companies", companies);
        }

        return "admin/index";
    }

}
