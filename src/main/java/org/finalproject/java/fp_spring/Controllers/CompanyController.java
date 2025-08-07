package org.finalproject.java.fp_spring.Controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import org.finalproject.java.fp_spring.Models.Company;
import org.finalproject.java.fp_spring.Services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/admin/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public String index(Model model, @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endDate,
            @RequestParam(name = "page", required = false) Integer page) {

        if (page == null || page < 0) {
            page = 0;
        }

        Page<Company> companies = companyService.GetAllFiltered(name, email, phone, startDate, endDate, page);
        model.addAttribute("companies", companies.toList());

        int[] pages = new int[companies.getTotalPages()];

        for (int i = 0; i < pages.length; i++) {
            pages[i] = i;
        }

        model.addAttribute("totalPages", pages);

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "company/index";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String show(Model model, @PathVariable("id") Integer id) {

        Optional<Company> company = companyService.show(id);
        if (company.isEmpty()) {
            return "admin/404";
        }
        model.addAttribute("company", company.get());
        return "company/show";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String create(Model model) {

        model.addAttribute("isEdit", false);

        Company company = new Company();
        model.addAttribute("company", company);
        return "company/create";
    }

    @PostMapping("/store")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String store(@Valid @ModelAttribute("company") Company company, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "company/create";
        }
        companyService.save(company);
        return "redirect:/admin/company";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String edit(Model model, @PathVariable("id") Integer id) {
        Optional<Company> company = companyService.show(id);

        model.addAttribute("isEdit", true);

        if (company.isEmpty()) {
            return "company/404";
        }

        model.addAttribute("company", company);
        return "company/create";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String update(@Valid @ModelAttribute("company") Company company, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("company", companyService.show(company.getId()));
            return "company/create";
        }

        
        Optional<Company> findCompany = companyService.show(company.getId());
        Company companyToUpdate = findCompany.get();

        companyToUpdate.setName(company.getName());
        companyToUpdate.setDescription(company.getDescription());
        companyToUpdate.setEmail(company.getEmail());
        companyToUpdate.setPhone(company.getPhone());
        companyToUpdate.setPIva(company.getPIva());

        companyService.save(companyToUpdate);
       
        return "redirect:/admin/company/" + company.getId();
    }

}
