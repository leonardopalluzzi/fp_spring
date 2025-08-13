package org.finalproject.java.fp_spring.Services;

import java.util.UUID;

import org.finalproject.java.fp_spring.Models.CompanyService;
import org.finalproject.java.fp_spring.Repositories.ServiceRepository;
import org.finalproject.java.fp_spring.Services.Interfaces.IServiceService;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceService implements IServiceService {

    @Autowired
    private ServiceRepository serviceRepo;

    @Override
    public String generateServiceCode() {
        return "SVC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public CompanyService createService(CompanyService service) {

        String code;

        // check if code already exists
        do {
            code = generateServiceCode();
        } while (serviceRepo.existsByCode(code));

        service.setCode(code);

        return serviceRepo.save(service);
    }
}
