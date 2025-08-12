package org.finalproject.java.fp_spring.ViewModels;

import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.Models.User;

public class UsersVM {

    private List<User> employees = new ArrayList<>();
    private List<User> customers = new ArrayList<>();

    public UsersVM(List<User> employees, List<User> customers) {
        this.customers = customers;
        this.employees = employees;
    }

    public List<User> getEmployees() {
        return this.employees;
    }

    public void setEmployees(List<User> employees) {
        this.employees = employees;
    }

    public List<User> getCustomers() {
        return this.customers;
    }

    public void setCustomers(List<User> customers) {
        this.customers = customers;
    }

}
