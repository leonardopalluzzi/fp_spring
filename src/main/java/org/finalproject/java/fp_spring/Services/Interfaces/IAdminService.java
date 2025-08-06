package org.finalproject.java.fp_spring.Services.Interfaces;

import java.util.List;

import org.finalproject.java.fp_spring.Models.Company;

public interface IAdminService {

    public List<Company> GetAllPaginated();

}
