package org.finalproject.java.fp_spring.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.finalproject.java.fp_spring.DTOs.UserInputDTO;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Ticket> userTickets = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Ticket> adminTickets = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "operators", fetch = FetchType.EAGER)
    private List<CompanyService> services = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company_id") // nome della colonna FK in tabella user
    private Company company;

    @ManyToMany(mappedBy = "customers", fetch = FetchType.EAGER)
    private List<CompanyService> customerServices;

    public User() {

    }

    public User(UserInputDTO userDTO) {
        this.username = userDTO.getUsername();
        this.email = userDTO.getEmail();
        this.password = userDTO.getPassword();
    }

    public List<CompanyService> getCustomerServices() {
        return customerServices;
    }

    public void setCustomerServices(List<CompanyService> customerServices) {
        this.customerServices = customerServices;
    }

    public List<CompanyService> getServices() {
        return services;
    }

    public void setServices(List<CompanyService> services) {
        this.services = services;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Ticket> getUserTickets() {
        return this.userTickets;
    }

    public void setUserTickets(List<Ticket> userTickets) {
        this.userTickets = userTickets;
    }

    public List<Ticket> getAdminTickets() {
        return this.adminTickets;
    }

    public void setAdminTickets(List<Ticket> adminTickets) {
        this.adminTickets = adminTickets;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
