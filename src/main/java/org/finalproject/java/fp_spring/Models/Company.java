package org.finalproject.java.fp_spring.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.finalproject.java.fp_spring.Enum.RoleName;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;

    @Size(min = 10, max = 1200, message = "Description must be between 10 and 1200 characters")
    private String description;

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "Phone cannot be blank")
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be numeric and can start with +")
    private String phone;

    @NotBlank(message = "P.IVA cannot be blank")
    @Pattern(regexp = "^[0-9]{11}$", message = "P.IVA must have 11 digits")
    private String pIva;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CompanyService> services = new ArrayList<>();

    // restituisce gli admin di una company
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    public Company() {

    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPIva() {
        return this.pIva;
    }

    public void setPIva(String pIva) {
        this.pIva = pIva;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CompanyService> getServices() {
        return this.services;
    }

    public void setServices(List<CompanyService> services) {
        this.services = services;
    }

    // helpers
    public void addUser(User user) {
        this.users.add(user);
        user.setCompany(this); // ESSENZIALE per mantenere la relazione sincronizzata
    }

    // helpers
    public long getAdminCount() {
        return getUsers().stream().filter(u -> u.getRoles().stream()
                .anyMatch(r -> r.getName().toString().equals(RoleName.COMPANY_ADMIN.toString()))).count();
    }
}
