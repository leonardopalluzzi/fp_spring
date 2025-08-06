package org.finalproject.java.fp_spring.Controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
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
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDate) {

        List<Company> companies = companyService.GetAllFiltered(name, email, phone, startDate, endDate);
        model.addAttribute("companies", companies);

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/index";
    }

    // @GetMapping("/search")
    // @PreAuthorize("hasAuthority('ADMIN')")
    // public String search(Model model, @RequestParam(name = "name", required =
    // false) String name,
    // @RequestParam(name = "email", required = false) String email,
    // @RequestParam(name = "phone", required = false) String phone,
    // @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern =
    // "yyyy-MM-dd'T'HH:mm") LocalDateTime startDate,
    // @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern =
    // "yyyy-MM-dd'T'HH:mm") LocalDateTime endDate) {

    // List<Company> companies;

    // if (email != null || phone != null || startDate != null || endDate != null) {
    // System.out.println("case 1");
    // companies = companyService.GetAllFiltered(email, phone, startDate, endDate);

    // } else if (name != null) {
    // System.out.println("case 2");

    // companies = companyService.GetAllPaginatedByName(name);

    // } else {
    // System.out.println("case 3");

    // companies = companyService.GetAllPaginated();

    // }

    // model.addAttribute("companies", companies);

    // return "admin/index";
    // }

}
