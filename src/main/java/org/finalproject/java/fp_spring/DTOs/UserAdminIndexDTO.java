package org.finalproject.java.fp_spring.DTOs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class UserAdminIndexDTO {

    Page<UserDTO> customers = new PageImpl<UserDTO>(new ArrayList<UserDTO>());

    Page<UserDTO> employees = new PageImpl<UserDTO>(new ArrayList<UserDTO>());

    List<UserDTO> companyAdmins = new ArrayList<>();

    public Page<UserDTO> getCustomers() {
        return this.customers;
    }

    public void setCustomers(Page<UserDTO> customers) {
        this.customers = customers;
    }

    public Page<UserDTO> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Page<UserDTO> employees) {
        this.employees = employees;
    }

    public List<UserDTO> getCompanyAdmins() {
        return this.companyAdmins;
    }

    public void setCompanyAdmins(List<UserDTO> companyAdmins) {
        this.companyAdmins = companyAdmins;
    }

}
